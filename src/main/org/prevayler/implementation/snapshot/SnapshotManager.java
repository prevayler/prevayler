//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.snapshot;

import java.io.*;

import org.prevayler.foundation.*;


/** Writes and reads snapshots to/from files. You can extend this class to use a serialization mechanism other than Java's. E.g: XML.
 */
public class SnapshotManager {

    //only here to allow for subclases to call init method rather than super
    //please don't use.
    SnapshotManager() {}

	SnapshotManager(Object newPrevalentSystem) {
		nullInit(newPrevalentSystem);
	}

    // this is only here for NullSnapshotManager support
	protected void nullInit(Object newPrevalentSystem) {
		_recoveredPrevalentSystem = newPrevalentSystem;
		_recoveredVersion = 0;
		_directory = null;
	}

	private File _directory;
	private Object _recoveredPrevalentSystem;
	private long _recoveredVersion;


	/**
     * @param newPrevalentSystem The prevalentSystem to serialize/deserialize
     * @param snapshotDirectoryName The path of the directory where the last snapshot file will be read and where the new snapshot files will be created.
	 */
	public SnapshotManager(Object newPrevalentSystem, String snapshotDirectoryName) throws ClassNotFoundException, IOException {
		init(newPrevalentSystem, snapshotDirectoryName);
	}

    /**
     * @param newPrevalentSystem The prevalentSystem to serialize/deserialize
     * @param snapshotDirectoryName The path of the directory where the last snapshot file will be read and where the new snapshot files will be created.
	 */
	protected void init(Object newPrevalentSystem, String snapshotDirectoryName) throws ClassNotFoundException, IOException {
		_directory = FileManager.produceDirectory(snapshotDirectoryName);
		_recoveredVersion = latestVersion();
		_recoveredPrevalentSystem = _recoveredVersion == 0
			? newPrevalentSystem
			: readSnapshot(_recoveredVersion);
	}

	public Object recoveredPrevalentSystem() { return _recoveredPrevalentSystem; }


	public long recoveredVersion() { return _recoveredVersion; }


	public void writeSnapshot(Object prevalentSystem, long version) throws IOException {
		File tempFile = File.createTempFile("snapshot" + version + "temp", "generatingSnapshot", _directory);

		writeSnapshot(prevalentSystem, tempFile);

		File permanent = snapshotFile(version);
		permanent.delete();
		if (!tempFile.renameTo(permanent)) throw new IOException("Temporary snapshot file generated: " + tempFile + "\nUnable to rename it permanently to: " + permanent);
	}


	private void writeSnapshot(Object prevalentSystem, File snapshotFile) throws IOException {
        OutputStream out = new FileOutputStream(snapshotFile);
        try {
            writeSnapshot(prevalentSystem, out);
        } finally {
            out.close();
        }
	}


	/** Serializes prevalentSystem and writes it to snapshotFile. You can overload this method to use a serialization mechanism other than Java's. E.g: XML.
	*/
    public void writeSnapshot(Object prevalentSystem, OutputStream out) throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(out);
        stream.writeObject(prevalentSystem);
    }


    /** Returns "snapshot", the default suffix/extension for snapshot files. You can overload this method and return a different suffix if you want. E.g: "XmlSnapshot"
	*/
	protected String suffix() {
		return "snapshot";
	}


	/** Returns zero if no snapshot file was found.
	*/
	private long latestVersion() throws IOException {
		String[] fileNames = _directory.list();
		if (fileNames == null) throw new IOException("Error reading file list from directory " + _directory);

		long result = 0;
		for (int i = 0; i < fileNames.length; i++) {
			long candidate = version(fileNames[i]);
			if (candidate > result) result = candidate;
		}
		return result;
	}


	private Object readSnapshot(long version) throws ClassNotFoundException, IOException {
		File snapshotFile = snapshotFile(version);
		return readSnapshot(snapshotFile);
	}


	private Object readSnapshot(File snapshotFile) throws ClassNotFoundException, IOException {
        FileInputStream in = new FileInputStream(snapshotFile);
        try {
            return readSnapshot(in);
        } finally { in.close(); }
	}


	/** Deserializes and returns the object contained in snapshotFile. You can overload this method to use a deserialization mechanism other than Java's. E.g: XML.
	*/
    public Object readSnapshot(InputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(in);
        return ois.readObject();
    }


    private File snapshotFile(long version) {
		String fileName = "0000000000000000000" + version;
		return new File(_directory, fileName.substring(fileName.length() - 19) + "." + suffix());
	}


	/** Returns -1 if fileName is not the name of a snapshot file.
	*/
	private long version(String fileName) {
		if (!fileName.endsWith("." + suffix())) return -1;
		return Long.parseLong(fileName.substring(0, fileName.indexOf("." + suffix())));    // "00000.snapshot" becomes "00000".
	}


	public Object deepCopy(Object original, String errorMessage) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeSnapshot(original, out);
			return readSnapshot(new ByteArrayInputStream(out.toByteArray()));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(errorMessage);
		}
	}

}
