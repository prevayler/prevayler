// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import java.io.*;

/** Creates .log and .snapshot files using a number sequence for the file name.
*/
class FileCreator {

	static final String SNAPSHOT_SUFFIX = ".snapshot";
	static final String LOG_SUFFIX = ".commandLog";
	
	static String snapshotFileName(long fileNumber) {
		String fileName = "000000000000000000000" + fileNumber;
		return fileName.substring(fileName.length() - 21) + FileCreator.SNAPSHOT_SUFFIX;
	}
	
	static String logFileName(long fileNumber) {
		String fileName = "000000000000000000000" + fileNumber;
		return fileName.substring(fileName.length() - 21) + FileCreator.LOG_SUFFIX;
	}

	private long nextFileNumber;


	FileCreator(long firstFileNumber) {
		this.nextFileNumber = firstFileNumber;
	}

	File newLog(File directory) throws IOException {
		File log = new File(directory, FileCreator.logFileName(nextFileNumber));
		if(!log.createNewFile()) throw new IOException("Attempt to create command log file that already existed: " + log);;

		nextFileNumber++;
		return log;
	}

	File newSnapshot(File directory) throws IOException {
		File snapshot = new File(directory, FileCreator.snapshotFileName(nextFileNumber - 1));
		snapshot.delete();   //If no commands are logged, the same snapshot file will be created over and over.
		return snapshot;
	}

	File newTempSnapshot(File directory) throws IOException {
		return File.createTempFile("temp","generatingSnapshot",directory);
	}
}
