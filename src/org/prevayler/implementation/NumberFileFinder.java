// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import java.io.*;
import java.util.Arrays;
import java.text.*;

/** Finds the last .snapshot file by number and finds all the subsequent pending .log files.
*/
class NumberFileFinder {

    private File directory;
	private File lastSnapshot;
    private long fileNumber;
    private NumberFileCreator fileCreator;

    /** @throws IOException if location does not exist and cannot be created as a directory.
    * @throws IllegalArgumentException If location exists and is not a directory.
    */
    public NumberFileFinder(String directoryName) throws IOException {
	this.directory = new File(directoryName);
	if (!directory.exists() && !directory.mkdirs()) throw new IOException("Directory doesn't exist and could not be created: " + directoryName);
	if (!directory.isDirectory()) throw new IllegalArgumentException("Path exists but is not a directory: " + directoryName);

	init();
	}

    /** Returns the last snapshot file. Returns null if there are no snapshots.
    */
    public File lastSnapshot() {
	return lastSnapshot;
	}

    /** @throws EOFException if there are no more pending .log files.
    */
    public File nextPendingLog() throws EOFException {
	File log = new File(directory, NumberFileCreator.LOG_FORMAT.format(fileNumber + 1));
	if (!log.exists()) {
	    fileCreator = new NumberFileCreator(directory, fileNumber + 1);
	    throw new EOFException();
	}
	++fileNumber;
	return log;
	}

    /** Returns a NumberFileCreator that will start off with the first number after the last .log file number.
    * @return null if there are still .log files pending.
    */
    public NumberFileCreator fileCreator() {
	return fileCreator;
    }

    private void init() throws IOException {
	findLastSnapshot();
	fileNumber = lastSnapshot == null
	    ? 0
	    : number(lastSnapshot);
	}

    private long number(File snapshot) throws NumberFormatException {  //NumberFomatException is a RuntimeException.
	String name = snapshot.getName();
	if (!name.endsWith("." + NumberFileCreator.SNAPSHOT_SUFFIX)) throw new NumberFormatException();
	return Long.parseLong(name.substring(0,name.indexOf('.')));    // "00000.snapshot" becomes "00000".
	//The following doesn't work! It throws ParseException (UnparseableNumber): return (NumberFileCreator.SNAPSHOT_FORMAT.parse(snapshot.getName())).longValue();
	}

    private void findLastSnapshot() throws IOException {
	File[] snapshots = directory.listFiles(new SnapshotFilter());
	if (snapshots == null) throw new IOException("Error reading file list from directory " + directory);

	Arrays.sort(snapshots);

	lastSnapshot = snapshots.length > 0
	    ? snapshots[snapshots.length - 1]
	    : null;
	}


    private class SnapshotFilter implements FileFilter {

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
