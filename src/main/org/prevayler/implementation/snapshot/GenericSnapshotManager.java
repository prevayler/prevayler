package org.prevayler.implementation.snapshot;

import org.prevayler.foundation.FileManager;
import org.prevayler.foundation.serialization.JavaSerializer;
import org.prevayler.foundation.serialization.Serializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class GenericSnapshotManager {

	private Map _strategies;
	private String _primarySuffix;
	private File _directory;
	private long _recoveredVersion;
	private Object _recoveredPrevalentSystem;

	public GenericSnapshotManager(Serializer serializer, Object newPrevalentSystem, String snapshotDirectoryName)
			throws IOException, ClassNotFoundException {
		this(serializer, "snapshot", newPrevalentSystem, snapshotDirectoryName);
	}

	public GenericSnapshotManager(Serializer serializer, String suffix, Object newPrevalentSystem,
								  String snapshotDirectoryName)
			throws IOException, ClassNotFoundException {
		this(Collections.singletonMap(suffix, serializer), suffix, newPrevalentSystem, snapshotDirectoryName);
	}

	public GenericSnapshotManager(Map strategies, String primarySuffix, Object newPrevalentSystem,
								  String snapshotDirectoryName)
			throws IOException, ClassNotFoundException {
		for (Iterator iterator = strategies.keySet().iterator(); iterator.hasNext();) {
			String suffix = (String) iterator.next();
			checkValidSuffix(suffix);
		}

		if (!strategies.containsKey(primarySuffix)) {
			throw new IllegalArgumentException("Primary suffix '" + primarySuffix + "' does not appear in strategies map");
		}

		_strategies = strategies;
		_primarySuffix = primarySuffix;

		_directory = FileManager.produceDirectory(snapshotDirectoryName);

		File latestSnapshot = latestSnapshot(_directory);
		_recoveredVersion = latestSnapshot == null ? 0 : version(latestSnapshot);
		_recoveredPrevalentSystem = latestSnapshot == null
				? newPrevalentSystem
				: readSnapshot(latestSnapshot);
	}

	GenericSnapshotManager(Object newPrevalentSystem) {
		_strategies = Collections.singletonMap("snapshot", new JavaSerializer());
		_primarySuffix = "snapshot";
		_directory = null;
		_recoveredVersion = 0;
		_recoveredPrevalentSystem = newPrevalentSystem;
	}


	public Serializer primarySerializer() {
		return (Serializer) _strategies.get(_primarySuffix);
	}

	public Object recoveredPrevalentSystem() {
		return _recoveredPrevalentSystem;
	}

	public long recoveredVersion() {
		return _recoveredVersion;
	}

	public void writeSnapshot(Object prevalentSystem, long version) throws IOException {
		File tempFile = File.createTempFile("snapshot" + version + "temp", "generatingSnapshot", _directory);

		writeSnapshot(prevalentSystem, tempFile);

		File permanent = snapshotFile(version);
		permanent.delete();
		if (!tempFile.renameTo(permanent)) throw new IOException(
				"Temporary snapshot file generated: " + tempFile + "\nUnable to rename it permanently to: " + permanent);
	}

	private void writeSnapshot(Object prevalentSystem, File snapshotFile) throws IOException {
		OutputStream out = new FileOutputStream(snapshotFile);
		try {
			primarySerializer().writeObject(out, prevalentSystem);
		} finally {
			out.close();
		}
	}


	private File snapshotFile(long version) {
		return snapshotFile(version, _directory, _primarySuffix);
	}

	private static File snapshotFile(long version, File directory, String suffix) {
		String fileName = "0000000000000000000" + version;
		return new File(directory, fileName.substring(fileName.length() - DIGITS_IN_SNAPSHOT_FILENAME) + "." + suffix);
	}

	private Object readSnapshot(File snapshotFile) throws ClassNotFoundException, IOException {
		String suffix = snapshotFile.getName().substring(snapshotFile.getName().indexOf('.') + 1);
		if (!_strategies.containsKey(suffix)) throw new IOException(
				snapshotFile.toString() + " cannot be read; only " + _strategies.keySet().toString() + " supported");

		Serializer serializer = (Serializer) _strategies.get(suffix);
		FileInputStream in = new FileInputStream(snapshotFile);
		try {
			return serializer.readObject(in);
		} finally {
			in.close();
		}
	}


	private static final int DIGITS_IN_SNAPSHOT_FILENAME = 19;
	private static final String SNAPSHOT_SUFFIX_PATTERN = "[a-zA-Z0-9]*[Ss]napshot";
	private static final String SNAPSHOT_FILENAME_PATTERN = "\\d{" + DIGITS_IN_SNAPSHOT_FILENAME + "}\\." +
			SNAPSHOT_SUFFIX_PATTERN;

	private static void checkValidSuffix(String suffix) {
		if (!suffix.matches(SNAPSHOT_SUFFIX_PATTERN)) {
			throw new IllegalArgumentException(
					"Snapshot filename suffix must match /" + SNAPSHOT_SUFFIX_PATTERN + "/, but '" + suffix + "' does not");
		}
	}

	/**
	 * Returns -1 if fileName is not the name of a snapshot file.
	 */
	private static long version(File file) {
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
			long candidateVersion = version(candidateSnapshot);
			if (candidateVersion > latestVersion) {
				latestVersion = candidateVersion;
				latestSnapshot = candidateSnapshot;
			}
		}
		return latestSnapshot;
	}

}
