// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import org.prevayler.*;
import java.io.*;
import java.util.*;


/**
 * Recovers the state of a PrevalentSystem by reading the last snapshot file, if any, reading commands from the pending commandLog files and "reexecuting" them.
 * @see CommandLogRecoverer
 */
class SystemRecoverer {

	private PrevalentSystem system;

	private final File snapshotDirectory;
	private final File[] commandLogDirectories;

	private File currentLogFile;
	private long nextLogFileNumber;


	SystemRecoverer(File snapshotDirectory, File[] commandLogDirectories) {
		this.snapshotDirectory = snapshotDirectory;
		this.commandLogDirectories = commandLogDirectories;
	}


	PrevalentSystem recover(PrevalentSystem newSystem) throws ClassNotFoundException, IOException {
		File snapshotFile = findLastSnapshotFile(snapshotDirectory);

		if (snapshotFile == null) {
			system = newSystem;
			nextLogFileNumber = 1;
		} else {
			system = readSystem(snapshotFile);
			nextLogFileNumber = number(snapshotFile) + 1;
		}

		recoverPendingCommands();

		return system;
	}


	long nextLogFileNumber() {
		return nextLogFileNumber;
	}


	private void recoverPendingCommands() throws IOException, ClassNotFoundException {
		long nextCommandNumber = 1;
		final List logRecoverers = new LinkedList();

		while (nextLogFile()) {
			CommandLogRecoverer newLogRecoverer = new CommandLogRecoverer(currentLogFile, system);
			if (newLogRecoverer.isExecutionSequenceRestarted()) {
				nextCommandNumber = 1;
				clearLogRecoverers(logRecoverers);
			}
			logRecoverers.add(0, newLogRecoverer);

			while(recoverNextCommand(nextCommandNumber, logRecoverers)) {
				nextCommandNumber++;
			}
		}
	}


	private boolean recoverNextCommand(long nextCommandNumber, List logRecoverers) throws IOException, ClassNotFoundException {
		Iterator iterator = logRecoverers.iterator();
		while (iterator.hasNext()) {
			CommandLogRecoverer logRecoverer = (CommandLogRecoverer)iterator.next();

			try {
				if (logRecoverer.recover(nextCommandNumber)) return true;
			} catch (EOFException eof) {
				logRecoverer.close();
				iterator.remove();
			}
		}
		return false;
	}


	private boolean nextLogFile() throws IOException {
		for (int i = 0; i < commandLogDirectories.length; i++) {
			currentLogFile = new File(commandLogDirectories[i], FileCreator.logFileName(nextLogFileNumber));
			if (currentLogFile.exists()) {
				nextLogFileNumber++;
				return true;
			}
		}
		return false;
	}


	static private void clearLogRecoverers(List logRecoverers) throws IOException {
		Iterator iterator = logRecoverers.iterator();
		while (iterator.hasNext()) {
			((CommandLogRecoverer)iterator.next()).close();
		}
		logRecoverers.clear();
	}

	
	static private File findLastSnapshotFile(File directory) throws IOException {
		File[] snapshots = directory.listFiles(new SnapshotFilter());
		if (snapshots == null) throw new IOException("Error reading file list from directory " + directory);

		Arrays.sort(snapshots);

		return snapshots.length > 0
			? snapshots[snapshots.length - 1]
			: null;
	}


	static private long number(File snapshot) throws NumberFormatException {  //NumberFomatException is a RuntimeException.
		String name = snapshot.getName();
		if (!name.endsWith(FileCreator.SNAPSHOT_SUFFIX)) throw new NumberFormatException();
		return Long.parseLong(name.substring(0,name.indexOf('.')));    // "00000.snapshot" becomes "00000". The following doesn't work (it throws ParseException (UnparseableNumber)): return (NumberFileCreator.SNAPSHOT_FORMAT.parse(snapshot.getName())).longValue();
	}


	static private PrevalentSystem readSystem(File snapshotFile) throws ClassNotFoundException, IOException {
		ObjectInputStream ois = objectInputStream(snapshotFile);
		try {
			return (PrevalentSystem)ois.readObject();
		} finally {
			ois.close();
		}
	}


	static private ObjectInputStream objectInputStream(File file) throws IOException {
		System.out.println("Reading " + file + "...");
		return new ObjectInputStream(new FileInputStream(file));
	}


	static private class SnapshotFilter implements FileFilter {

		public boolean accept(File file) {
			try {
				number(file);
			} catch (NumberFormatException nfx) {
				return false;
			}
			return true;
		}
	}
}
