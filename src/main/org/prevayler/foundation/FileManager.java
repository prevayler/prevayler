//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.foundation;

import java.io.*;

public class FileManager {

	public static final int DIGITS_IN_SNAPSHOT_FILENAME = 19;
	public static final String SNAPSHOT_SUFFIX_PATTERN = "[a-zA-Z0-9]*[Ss]napshot";
	public static final String SNAPSHOT_FILENAME_PATTERN = "\\d{" + DIGITS_IN_SNAPSHOT_FILENAME + "}\\." +
			SNAPSHOT_SUFFIX_PATTERN;

	public static File produceDirectory(String directoryName) throws IOException {
		File directory = new File(directoryName);
		if (!directory.exists() && !directory.mkdirs()) throw new IOException("Directory doesn't exist and could not be created: " + directoryName);
		if (!directory.isDirectory()) throw new IOException("Path exists but is not a directory: " + directoryName);
		return directory;
	}

	public static File snapshotFile(long version, File directory, String suffix) {
		String fileName = "0000000000000000000" + version;
		return new File(directory, fileName.substring(fileName.length() - DIGITS_IN_SNAPSHOT_FILENAME) + "." + suffix);
	}

	public static void checkValidSnapshotSuffix(String suffix) {
		if (!suffix.matches(SNAPSHOT_SUFFIX_PATTERN)) {
			throw new IllegalArgumentException(
					"Snapshot filename suffix must match /" + SNAPSHOT_SUFFIX_PATTERN + "/, but '" + suffix + "' does not");
		}
	}

	/**
	 * Returns -1 if fileName is not the name of a snapshot file.
	 */
	public static long snapshotVersion(File file) {
		String fileName = file.getName();
		if (!fileName.matches(SNAPSHOT_FILENAME_PATTERN)) return -1;
		return Long.parseLong(fileName.substring(0, fileName.indexOf(".")));    // "00000.snapshot" becomes "00000".
	}

	/**
	 * Find the latest snapshot file. Returns null if no snapshot file was found.
	 */
	public static File latestSnapshot(File directory) throws IOException {
		File[] files = directory.listFiles();
		if (files == null) throw new IOException("Error reading file list from directory " + directory);

		File latestSnapshot = null;
		long latestVersion = 0;
		for (int i = 0; i < files.length; i++) {
			File candidateSnapshot = files[i];
			long candidateVersion = snapshotVersion(candidateSnapshot);
			if (candidateVersion > latestVersion) {
				latestVersion = candidateVersion;
				latestSnapshot = candidateSnapshot;
			}
		}
		return latestSnapshot;
	}

}
