// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import java.util.*;
import java.io.*;
import org.prevayler.*;


/**
 * Provides transparent persistence for business objects.
 * This applies to any deterministic system implementing the PrevalentSystem interface.
 * All commands to the system must be represented as objects implementing the Command interface and must be executed using Prevayler.executeCommand(Command).
 * Take a look at the demo application included with the Prevayler distribution for examples.

 * Forces a FileDescriptor.sync() to guarantee ... . This can be turned off, for improved performance, by setting the org.prevayler.SafeCommandLogs system property to "OFF".
 * "org.prevayler.SafeCommandLogs"
 * "org.prevayler.CommandLogsThresholdBytes"
 * "org.prevayler.CommandLogsThresholdMinutes"
 * @see #java.io.FileDescriptor.sync()
 */
public class SnapshotPrevayler implements Prevayler {

	private final PrevalentSystem system;
	private final SystemClock clock;

	private final PrevalenceBase prevalenceBase;

	private volatile int commandsBeingExecuted = 0;   //Snapshots can only be taken when this number is zero.
	private final Object commandExecutionMonitor = new Object();
	private final Object snapshotMonitor = new Object();
	private final List commandQueue = new LinkedList();


	public SnapshotPrevayler(PrevalentSystem newSystem) throws IOException, ClassNotFoundException {
		this(newSystem, null, null, null);
	}

	public SnapshotPrevayler(PrevalentSystem newSystem, String prevalenceDirectory) throws IOException, ClassNotFoundException {
		this(newSystem, null, prevalenceDirectory, null);
	}

	public SnapshotPrevayler(PrevalentSystem newSystem, String prevalenceDirectory, int logFiles) throws IOException, ClassNotFoundException {
		this(newSystem, fillArray(prevalenceDirectory, logFiles), prevalenceDirectory, null);
	}

	public SnapshotPrevayler(PrevalentSystem newSystem, String prevalenceDirectory, SystemClock systemClock) throws IOException, ClassNotFoundException {
		this(newSystem, null, prevalenceDirectory, systemClock);
	}

	public SnapshotPrevayler(PrevalentSystem newSystem, String[] commandLogDirectories, String snapshotDirectory) throws IOException, ClassNotFoundException {
		this(newSystem, commandLogDirectories, snapshotDirectory, null);
	}

	/** Returns a new SnapshotPrevayler for the given PrevalentSystem.
	* @param newSystem The newly started, "empty" PrevalentSystem that will be used as a starting point for every system startup, until the first snapshot is taken.
	* @param systemClock The "real" clock to be used by the system.
	* @param snapshotDirectory The full path of the directory where the last snapshot file will be read and where the new snapshot files will be created.
	* @param commandLogDirectories The full paths of the directories where the pending log files will be read and where the new log files will be created.
	*/
	public SnapshotPrevayler(PrevalentSystem newSystem, String[] commandLogDirectories, String snapshotDirectory, SystemClock systemClock) throws IOException, ClassNotFoundException {
		if (snapshotDirectory == null) snapshotDirectory = "PrevalenceBase";
		if (commandLogDirectories == null) {
			commandLogDirectories = fillArray(snapshotDirectory, 5);
		}
		if (systemClock == null) systemClock = new SystemClock();

		prevalenceBase = new PrevalenceBase(snapshotDirectory, commandLogDirectories);

		newSystem.clock(systemClock);
		system = prevalenceBase.recoverSystem(newSystem);

		this.clock = (SystemClock)system.clock();
		clock.resume();
	}


	/** Returns the underlying PrevalentSystem.
	*/
	public PrevalentSystem system() {
		return system;
	}


	/** Produces a complete serialized image of the underlying PrevalentSystem.
	* This will accelerate future system startups. Taking a snapshot once a day is enough for most applications.
	* Subsequent calls to executeCommand() will be blocked until the snapshot is taken.
	* @see #system()
	* @throws IOException if there is trouble writing to the snapshot file.
	*/
	public void takeSnapshot() throws IOException {
		synchronized (snapshotMonitor) {
			while (commandsBeingExecuted != 0) Thread.yield();

			clock.pause();
			try {

				prevalenceBase.writeSnapshot(system);

			} finally {
				clock.resume();
			}
		}
	}


	/** Logs the received command for crash or shutdown recovery and executes it on the underlying PrevalentSystem.
	* @see #system()
	* @return The serializable object that was returned by the execution of command.
	* @throws IOException if there is trouble writing the command to one of the log files.
	* @throws Exception if command.execute(PrevalentSystem) throws an exception.
	*/
	public Serializable executeCommand(Command command) throws Exception {
		prepareForMyTurn(command);
		try {

			return command.execute(system);

		} finally {
			endMyTurn();
		}
	}


	private void prepareForMyTurn(Command command) throws IOException {
		commandExecutionStarting();
		CommandLogger myCommandLogger = prevalenceBase.availableCommandLogger();
		myCommandLogger.writeCommand(command);   //Command serialization is done in parallel.

		Object myPlaceHolder = new Object();
		long myExecutionTime;

		synchronized (commandQueue) {
			if (commandQueue.isEmpty()) clock.pause();  //To be deterministic, time cannot vary during the execution of a command.
			commandQueue.add(myPlaceHolder);

			myExecutionTime = clock.currentTimeMillis();    //I don't use clock.time() because another thread might have paused the clock a long time ago.
			prevalenceBase.generateExecutionSequence(myCommandLogger, myExecutionTime);
		}

		prevalenceBase.flushToDisk(myCommandLogger);   //Flushing to disk is done in parallel.

		waitForMyTurn(myPlaceHolder);   //Command execution is done sequentially.
		clock.recover(myExecutionTime);
	}

	private void waitForMyTurn(Object myPlaceHolder) {
		synchronized (myPlaceHolder) {
			Object firstPlaceHolder;
			synchronized (commandQueue) {
				firstPlaceHolder = commandQueue.get(0);
			}
			try {
				if (firstPlaceHolder != myPlaceHolder) myPlaceHolder.wait();
			} catch (InterruptedException ix) {
				throw new RuntimeException("Unexpected InterruptedException.");
			}
		}
	}

	private void endMyTurn() {
		Object nextPlaceHolder = null;
		synchronized (commandQueue) {
			commandQueue.remove(0);
			if (commandQueue.isEmpty()) {
				clock.resume();
			} else {
				nextPlaceHolder = commandQueue.get(0);
			}
		}
		
		if (nextPlaceHolder != null) {
			synchronized (nextPlaceHolder) {
				nextPlaceHolder.notify();
			}
		}
		
		commandExecutionFinishing();
	}


	private void commandExecutionStarting() {
		synchronized (snapshotMonitor) {
			synchronized (commandExecutionMonitor) {
				commandsBeingExecuted++;
			}
		}
	}

	private void commandExecutionFinishing() {
		synchronized (commandExecutionMonitor) {
			commandsBeingExecuted--;
		}
	}


	static private String[] fillArray(String element, int size) {
		String[] result = new String[size];
		Arrays.fill(result, element);
		return result;
	}
}
