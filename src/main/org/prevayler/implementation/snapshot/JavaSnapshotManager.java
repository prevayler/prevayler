//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Jacob Kjome.

package org.prevayler.implementation.snapshot;

import java.io.*;


/**
 * Writes and reads snapshots to/from files using standard Java serialization.
 */
public class JavaSnapshotManager extends AbstractSnapshotManager {

    //this is only here for NullSnapshotManager support
    JavaSnapshotManager(Object newPrevalentSystem) {
        nullInit(newPrevalentSystem);
    }

	/**
     * @param newPrevalentSystem The prevalentSystem to serialize/deserialize
     * @param snapshotDirectoryName The path of the directory where the last snapshot file will be read and where the new snapshot files will be created.
	 */
	public JavaSnapshotManager(Object newPrevalentSystem, String snapshotDirectoryName) throws ClassNotFoundException, IOException {
		init(newPrevalentSystem, snapshotDirectoryName);
	}


	/**
	 * @see org.prevayler.implementation.snapshot.SnapshotManager#writeSnapshot(Object, OutputStream)
	 */
    public void writeSnapshot(Object prevalentSystem, OutputStream out) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(out);
        try {
            oos.writeObject(prevalentSystem);
        } finally {
            if (oos != null) oos.close();
        }
    }


    /**
	 * @see org.prevayler.implementation.snapshot.SnapshotManager#readSnapshot(InputStream)
	 */
    public Object readSnapshot(InputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(in);
        try {
            return ois.readObject();
        } finally {
            if (ois != null) ois.close();
        }
    }

}
