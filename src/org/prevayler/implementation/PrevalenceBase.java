// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import org.prevayler.*;
import java.io.*;
import java.util.*;

/** Saves and recovers commands and snapshots to/from files.
*/
class PrevalenceBase {

	private final File snapshotDirectory;
	private final File[] commandLogDirectories;

	private FileCreator fileCreator;

	private final List availableLoggers = new LinkedList();
	private boolean loggersCreated = false;
	private long nextCommandSequence;

	PrevalenceBase(String snapshotDirectoryName, String[] commandLogDirectoryNames) throws IOException {
		snapshotDirectory = findDirectory(snapshotDirectoryName);
		commandLogDirectories = findDirectories(commandLogDirectoryNames);
	}

	
	PrevalentSystem recoverSystem(PrevalentSystem newSystem) throws ClassNotFoundException, IOException {
		SystemRecoverer recoverer = new SystemRecoverer(snapshotDirectory, commandLogDirectories);
		PrevalentSystem system = recoverer.recover(newSystem);

		fileCreator = new FileCreator(recoverer.nextLogFileNumber());

		return system;		
	}


	CommandLogger availableCommandLogger() throws IOException {
		synchronized (availableLoggers) {
			while (availableLoggers.isEmpty()) {
					if (loggersCreated) {
						waitForAvailableLogger();
					} else {
						createLoggers();
					}
			}

			return (CommandLogger)availableLoggers.remove(0);
		}
	}

	private void waitForAvailableLogger() {
		try {
			availableLoggers.wait();
		} catch (InterruptedException ix) {
			throw new RuntimeException("Unexpected InterruptedException.");
		}
	}

	private void createLoggers() throws IOException {
		createLogger(commandLogDirectories[0], true);
		for (int i = 1; i < commandLogDirectories.length; i++) {
			createLogger(commandLogDirectories[i], false);
		}

		nextCommandSequence = 1;
		loggersCreated = true;
	}

	private void createLogger(File directory, boolean sequenceRestarted) throws IOException {
		File logFile = fileCreator.newLog(directory);
		availableLoggers.add(new CommandLogger(logFile, sequenceRestarted));
	}


	void generateExecutionSequence(CommandLogger commandLogger, long executionTime) {
		commandLogger.executionSequence(executionTime, nextCommandSequence++);
	}


	void flushToDisk(CommandLogger commandLogger) throws IOException {
		commandLogger.flushToDisk();
		makeLoggerAvailable(commandLogger);
	}

	private void makeLoggerAvailable(CommandLogger commandLogger) throws IOException {
		synchronized (availableLoggers) {
			if (commandLogger.isValid()) {
				availableLoggers.add(commandLogger);
			} else {
				commandLogger.close();
				createLogger(commandLogger.directory(),false);
			}

			availableLoggers.notify();
		}
	}


	void writeSnapshot(PrevalentSystem system) throws IOException {
		closeLoggers();

		File tempSnapshot = fileCreator.newTempSnapshot(snapshotDirectory);

		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(tempSnapshot));
		stream.writeObject(system);
		stream.close();

		File snapshot = fileCreator.newSnapshot(snapshotDirectory);
		if (!tempSnapshot.renameTo(snapshot)) throw new IOException("Unable to rename " + tempSnapshot + " to " + snapshot);
	}

	private void closeLoggers() throws IOException {
		Iterator it = availableLoggers.iterator();
		while (it.hasNext()) {
			((CommandLogger)it.next()).close();
		}
		availableLoggers.clear();
		loggersCreated = false;
	}


	static private File findDirectory(String directoryName) throws IOException {
		File directory = new File(directoryName);
		if (!directory.exists() && !directory.mkdirs()) throw new IOException("Directory doesn't exist and could not be created: " + directoryName);
		if (!directory.isDirectory()) throw new IOException("Path exists but is not a directory: " + directoryName);
		return directory;
	}

	static private File[] findDirectories(String[] directoryNames) throws IOException {
		File[] directories = new File[directoryNames.length];
		for (int i = 0; i < directoryNames.length; i++) {
			directories[i] = findDirectory(directoryNames[i]);
		}
		return directories;
	}
}
