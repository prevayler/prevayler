// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import org.prevayler.*;
import org.prevayler.util.StopWatch;
import java.io.*;

public class CommandLogger {

	private final boolean safeCommandLogs = safeCommandLogsSystemProperty();
	private final long commandLogsThresholdBytes = parseSystemProperty("org.prevayler.CommandLogsThresholdBytes");
	private final long commandLogsThresholdMillis = 1000 * 60 * parseSystemProperty("org.prevayler.CommandLogsThresholdMinutes");

	private final File logFile;
	private final FileOutputStream fileOutputStream;
	private final ObjectOutputStream objectOutputStream;

	private long executionTime;
	private long commandSequence;

	private final StopWatch stopWatch = StopWatch.start();


	CommandLogger(File logFile, boolean sequenceRestarted) throws IOException {
		this.logFile = logFile;
		fileOutputStream = new FileOutputStream(logFile);
		objectOutputStream = new ObjectOutputStream(fileOutputStream);

		objectOutputStream.writeChar(sequenceRestarted ? 'R' : 'C');   //R - Restart. C - Continue.
		flushStreams(true);
	}


	void writeCommand(Command command) throws IOException {
		objectOutputStream.writeObject(command);
	}


	void commandSequence(long executionTime, long commandSequence) {
		this.executionTime = executionTime;
		this.commandSequence = commandSequence;
	}


	void flushToDisk() throws IOException {
		objectOutputStream.writeLong(executionTime);
		objectOutputStream.writeLong(commandSequence);

		flushStreams(safeCommandLogs);
	}


	private void flushStreams(boolean sync) throws IOException {
		objectOutputStream.flush();
		if (sync) fileOutputStream.getFD().sync();   //"Force all system buffers to synchronize with the underlying device. This method returns after all modified data and attributes of this FileDescriptor have been written to the relevant device(s). In particular, if this FileDescriptor refers to a physical storage medium, such as a file in a file system, sync will not return until all in-memory modified copies of buffers associated with this FileDesecriptor have been written to the physical medium. sync is meant to be used by code that requires physical storage (such as a file) to be in a known state For example, a class that provided a simple transaction facility might use sync to ensure that all changes to a file caused by a given transaction were recorded on a storage medium. sync only affects buffers downstream of this FileDescriptor. If any in-memory buffering is being done by the application (for example, by a BufferedOutputStream object), those buffers must be flushed into the FileDescriptor (for example, by invoking OutputStream.flush) before that data will be affected by sync." -- Java SDK1.3.1.
	}


	boolean isValid() {
		return !isExpired() && !isFull();
	}

	private boolean isExpired() {
		if (commandLogsThresholdMillis == 0) return false;
		return stopWatch.millisEllapsed() >= commandLogsThresholdMillis;
	}

	private boolean isFull() {
		if (commandLogsThresholdBytes == 0) return false;
		return logFile.length() >= commandLogsThresholdBytes;
	}


	void close() throws IOException {
		objectOutputStream.close();
	}


	File directory() {
		return logFile.getParentFile();
	}


	static private long parseSystemProperty(String propertyName) {
		try {
			return Long.parseLong(System.getProperty(propertyName));
		} catch (NumberFormatException nfx) {
			return 0;
		}
	}

	static private boolean safeCommandLogsSystemProperty() {
		boolean safe = !"off".equalsIgnoreCase(System.getProperty("org.prevayler.SafeCommandLogs"));
		safeCommandLogsMessage(safe);
		return safe;
	}

	static private void safeCommandLogsMessage(boolean safe) {
		if (!safe && lastTimeWasSafe) {
			out("\n=====================================");
			out("The org.prevayler.SafeCommandLogs system property is OFF.");
			out("Writes to the commandLog files will be cached for greater performance. In the event of a system crash, some of the last executed commands might be lost, though.");
			out("=====================================\n");
		}
		if (safe && !lastTimeWasSafe) {
			out("\n=====================================");
			out("The org.prevayler.SafeCommandLogs system property is ON.");
			out("Writes to the commandLog files will be flushed to the underlying device before each command is executed.");
			out("=====================================\n");
		}
		lastTimeWasSafe = safe;
	}
	static private boolean lastTimeWasSafe = true;


	static private void out(Object message) {
		System.out.println(message);
	}
}
