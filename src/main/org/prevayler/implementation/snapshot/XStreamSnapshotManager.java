//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Jacob Kjome.

package org.prevayler.implementation.snapshot;

import com.thoughtworks.xstream.XStream;
import org.prevayler.foundation.serialization.XStreamSerializationStrategy;

import java.io.File;
import java.io.IOException;


/**
 * @deprecated Use {@link XStreamSerializationStrategy} instead.
 */
public class XStreamSnapshotManager extends GenericSnapshotManager {

    public static final String SUFFIX = "xstreamsnapshot";

    /**
     * Creates a new XStreamSnapshotManager using a default XStream instance.
     * 
     * <p>This default instance uses the XppDriver for XStream if the XPP3 library is
     * available on the classpath.</p>
     * 
     * @param newPrevalentSystem the prevalent system to snapshot.
     * @param snapshotDirectoryName the directory name where the snapshot must be stored.
     * @throws ClassNotFoundException if some class from the system cannot be found. 
     * @throws IOException if there's a problem reading the latest snapshot.
     */
	public XStreamSnapshotManager(Object newPrevalentSystem, String snapshotDirectoryName) throws ClassNotFoundException, IOException {
		this(new XStream(), newPrevalentSystem, snapshotDirectoryName);
	}

	/**
	 * Creates a new XStreamSnapshotManager using a pre-configured XStream instance.
	 * 
	 * <p>It is recommended to use the XStream XppDriver (used in XStream by default)
     * to achieve maximum performance</p>
	 *  
	 * @param xstream a pre-configured XStream instance.
     * @param newPrevalentSystem the prevalent system to snapshot.
     * @param snapshotDirectoryName the directory name where the snapshot must be stored.
     * @throws ClassNotFoundException if some class from the system cannot be found. 
     * @throws IOException if there's a problem reading the latest snapshot.
	 */
	public XStreamSnapshotManager(XStream xstream, Object newPrevalentSystem, String snapshotDirectoryName) throws ClassNotFoundException, IOException {
		super(new XStreamSerializationStrategy(xstream), SUFFIX, newPrevalentSystem, snapshotDirectoryName);
	}


	/**
	 * @see org.prevayler.implementation.snapshot.AbstractSnapshotManager#suffix()
	 */
	protected String suffix() {
		return SUFFIX;
	}


	/**
	 * Find the latest snapshot file. Returns null if no snapshot file was found or if the latest snapshot
	 * does not have the suffix "xstreamsnapshot".
	 *
	 * @deprecated Use {@link #latestSnapshot(java.io.File)} instead.
	 */
	public static File latestSnapshotFile(File directory) throws IOException {
		return latestSnapshotFile(directory, SUFFIX);
	}

}
