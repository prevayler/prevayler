//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Jacob Kjome.

package org.prevayler.implementation.snapshot;

import org.prevayler.foundation.serialization.Deserializer;
import org.prevayler.foundation.serialization.JavaSerializationStrategy;
import org.prevayler.foundation.serialization.SerializationStrategy;
import org.prevayler.foundation.serialization.Serializer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Writes and reads snapshots to/from files using standard Java serialization.
 */
public class JavaSnapshotManager extends AbstractSnapshotManager {
	public static final String SUFFIX = "snapshot";

	private SerializationStrategy _strategy;

    //this is only here for NullSnapshotManager support
    JavaSnapshotManager(Object newPrevalentSystem) {
		_strategy = new JavaSerializationStrategy();
        nullInit(newPrevalentSystem);
    }

	/**
     * @param newPrevalentSystem The prevalentSystem to serialize/deserialize
     * @param snapshotDirectoryName The path of the directory where the last snapshot file will be read and where the new snapshot files will be created.
	 */
	public JavaSnapshotManager(Object newPrevalentSystem, String snapshotDirectoryName, ClassLoader loader) throws ClassNotFoundException, IOException {
		_strategy = new JavaSerializationStrategy(loader);
		init(newPrevalentSystem, snapshotDirectoryName);
	}


	/**
	 * @see org.prevayler.implementation.snapshot.SnapshotManager#writeSnapshot(Object, OutputStream)
	 */
    public void writeSnapshot(Object prevalentSystem, OutputStream out) throws IOException {
		Serializer serializer = _strategy.createSerializer(out);
        try {
            serializer.writeObject(prevalentSystem);
        } finally {
			serializer.flush();
        }
    }


    /**
	 * @see org.prevayler.implementation.snapshot.SnapshotManager#readSnapshot(InputStream)
	 */
    public Object readSnapshot(InputStream in) throws IOException, ClassNotFoundException {
		Deserializer deserializer = _strategy.createDeserializer(in);
        return deserializer.readObject();
    }

	protected String suffix() {
		return SUFFIX;
	}


	/**
     * Find the latest snapshot file. Returns null if no snapshot file was found.
	 */
	public static File latestSnapshotFile(File directory) throws IOException {
		return latestSnapshotFile(directory, SUFFIX);
	}
}
