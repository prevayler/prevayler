//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Jacob Kjome.

package org.prevayler.implementation.snapshot;

import org.prevayler.foundation.serialization.JavaSerializationStrategy;

import java.io.File;
import java.io.IOException;


/**
 * Writes and reads snapshots to/from files using standard Java serialization.
 */
public class JavaSnapshotManager extends GenericSnapshotManager {
	public static final String SUFFIX = "snapshot";

	/**
     * @param newPrevalentSystem The prevalentSystem to serialize/deserialize
     * @param snapshotDirectoryName The path of the directory where the last snapshot file will be read and where the new snapshot files will be created.
	 */
	public JavaSnapshotManager(Object newPrevalentSystem, String snapshotDirectoryName, ClassLoader loader) throws ClassNotFoundException, IOException {
		super(new JavaSerializationStrategy(loader), newPrevalentSystem, snapshotDirectoryName);
	}

	// this is only here for NullSnapshotManager support
	JavaSnapshotManager(Object newPrevalentSystem) {
		super(new JavaSerializationStrategy(), newPrevalentSystem);
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
