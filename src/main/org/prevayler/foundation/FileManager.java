//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Justin Sampson, Eric Bridgwater

package org.prevayler.foundation;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;

public class FileManager {

	private static final int DIGITS_IN_FILENAME = 19;
	private static final String SNAPSHOT_SUFFIX_PATTERN = "[a-zA-Z0-9]*[Ss]napshot";
	private static final String SNAPSHOT_FILENAME_PATTERN = "\\d{" + DIGITS_IN_FILENAME + "}\\." + SNAPSHOT_SUFFIX_PATTERN;
	private static final String JOURNAL_SUFFIX_PATTERN = "[a-zA-Z0-9]*[Jj]ournal";
	private static final String JOURNAL_FILENAME_PATTERN = "\\d{" + DIGITS_IN_FILENAME + "}\\." + JOURNAL_SUFFIX_PATTERN;

	private File _directory;

	public FileManager(String directory) {
		this(new File(directory));
	}

	public FileManager(File directory) {
		_directory = directory;
	}

	public void produceDirectory() throws IOException {
		if (!_directory.exists() && !_directory.mkdirs()) throw new IOException("Directory doesn't exist and could not be created: " + _directory);
		if (!_directory.isDirectory()) throw new IOException("Path exists but is not a directory: " + _directory);
	}


	public static void checkValidSnapshotSuffix(String suffix) {
		if (!suffix.matches(SNAPSHOT_SUFFIX_PATTERN)) {
			throw new IllegalArgumentException(
					"Snapshot filename suffix must match /" + SNAPSHOT_SUFFIX_PATTERN + "/, but '" + suffix + "' does not");
		}
	}

	public static void checkValidJournalSuffix(String suffix) {
		if (!suffix.matches(JOURNAL_SUFFIX_PATTERN)) {
			throw new IllegalArgumentException(
					"Journal filename suffix must match /" + JOURNAL_SUFFIX_PATTERN + "/, but '" + suffix + "' does not");
		}
	}


	public File snapshotFile(long version, String suffix) {
		checkValidSnapshotSuffix(suffix);
		return file(version, suffix);
	}

	public File journalFile(long transaction, String suffix) {
		checkValidJournalSuffix(suffix);
		return file(transaction, suffix);
	}

	private File file(long version, String suffix) {
		String fileName = "0000000000000000000" + version;
		return new File(_directory, fileName.substring(fileName.length() - DIGITS_IN_FILENAME) + "." + suffix);
	}


	/**
	 * Returns -1 if fileName is not the name of a snapshot file.
	 */
	public static long snapshotVersion(File file) {
		return version(file, SNAPSHOT_FILENAME_PATTERN);
	}

	/**
	 * Returns -1 if fileName is not the name of a journal file.
	 */
	public static long journalVersion(File file) {
		return version(file, JOURNAL_FILENAME_PATTERN);
	}

	private static long version(File file, String filenamePattern) {
		String fileName = file.getName();
		if (!fileName.matches(filenamePattern)) return -1;
		return Long.parseLong(fileName.substring(0, fileName.indexOf(".")));
	}


	/**
	 * Find the latest snapshot file. Returns null if no snapshot file was found.
	 */
	public File latestSnapshot() throws IOException {
		File[] files = _directory.listFiles();
		if (files == null) throw new IOException("Error reading file list from directory " + _directory);

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

	public long findInitialJournalFile(long initialTransactionWanted) {
		File[] journals = _directory.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().matches(JOURNAL_FILENAME_PATTERN);
			}
		});

		long[] versions = new long[journals.length];
		for (int i = 0; i < journals.length; i++) {
			versions[i] = journalVersion(journals[i]);
		}

		Arrays.sort(versions);
		int match = Arrays.binarySearch(versions, initialTransactionWanted);

		if (match >= 0) {
			// Exact match was found.
			return initialTransactionWanted;
		} else {
			// match == -insertionPoint - 1
			int insertionPoint = -(match + 1);
			if (insertionPoint == 0) {
				// There is no appropriate log file.
				return 0L;
			} else {
				// Use the next lower version.
				return versions[insertionPoint - 1];
			}
		}
	}


	public File createTempFile(String prefix, String suffix) throws IOException {
		return File.createTempFile(prefix, suffix, _directory);
	}

	public static void renameUnusedFile(File journalFile) {
		journalFile.renameTo(new File(journalFile.getAbsolutePath() + ".unusedFile" + System.currentTimeMillis()));
	}

}
