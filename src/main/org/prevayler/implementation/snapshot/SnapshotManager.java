//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Jacob Kjome.

package org.prevayler.implementation.snapshot;

import java.io.*;


/**
 * Interface to implement when Writing/Reading snapshots to/from files.
 */
public interface SnapshotManager {

	public Object recoveredPrevalentSystem();


	public long recoveredVersion();


	public void writeSnapshot(Object prevalentSystem, long version) throws IOException;


	/**
     * Serializes prevalentSystem and writes it to snapshotFile. Overload this method to use your preferred serialization mechanism.
	 */
    public void writeSnapshot(Object prevalentSystem, OutputStream out) throws IOException;


	/**
     * Deserializes and returns the object contained in snapshotFile. Overload this method to use your preferred deserialization mechanism.
	 */
    public Object readSnapshot(InputStream in) throws IOException, ClassNotFoundException;


//	public Object deepCopy(Object original, String errorMessage);

}
