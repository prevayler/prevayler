// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import org.prevayler.*;
import java.io.*;

class CommandLogRecoverer {

	private final File logFile;
	private final ObjectInputStream logStream;

	private final boolean isCommandSequenceRestarted;

	private final PrevalentSystem system;
	private final SystemClock clock;
	
	private Command pendingCommand;
	private long pendingCommandNumber;
	private long pendingCommandExecutionTime;


	CommandLogRecoverer(File logFile, PrevalentSystem system) throws IOException {
		out("Reading " + logFile + "...");

		this.logFile = logFile;
		logStream = new ObjectInputStream(new FileInputStream(logFile));
		this.system = system;
		this.clock = (SystemClock)system.clock();

		boolean isRestarted;
		try {
			isRestarted = (logStream.readChar() == 'R');
		} catch (EOFException eof) {
			isRestarted = false;
		}
		isCommandSequenceRestarted = isRestarted;
	}


	boolean isCommandSequenceRestarted() {
		return isCommandSequenceRestarted;
	}


	boolean recover(long nextNumber) throws EOFException, IOException, ClassNotFoundException {
		preparePendingCommand();
		if (nextNumber != pendingCommandNumber) return false;

		clock.recover(pendingCommandExecutionTime);
		try {

			pendingCommand.execute(system);

		} catch (Exception e) {
			//Don't do anything at all now, during recovery. This exception was already treated by the client when it was thrown the first time, during normal system execution.
		}

		pendingCommand = null;
		return true;
	}


	private void preparePendingCommand() throws EOFException, IOException, ClassNotFoundException {
		if (pendingCommand != null) return;   //Had already been prepared.

		try {

			pendingCommand = (Command)logStream.readObject();
			pendingCommandExecutionTime = logStream.readLong();
			pendingCommandNumber = logStream.readLong();

		} catch (StreamCorruptedException scx) {
			abort(scx);
		} catch (RuntimeException rx) {    //Some stream corruptions cause runtime exceptions in JDK1.3.1!
			abort(rx);
		}
	}


	private void abort(Exception exception) throws EOFException {
		out("" + exception + " (File: " + logFile + ")");
		out("Some commands might have been lost. Looking for the next command..." );
		throw new EOFException();
	}


	void close() throws IOException {
		logStream.close();
	}


	static private void out(String message) {
		System.out.println(message);
	}
}
