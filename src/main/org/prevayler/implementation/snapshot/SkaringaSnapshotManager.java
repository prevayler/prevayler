//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Alexandre Nodari, Jacob Kjome.

package org.prevayler.implementation.snapshot;

import org.prevayler.foundation.serialization.SkaringaSerializationStrategy;

import java.io.File;
import java.io.IOException;


/**
 * Writes and reads snapshots to/from XML files.
 *
 * <p>This implementation requires the <a href="http://www.skaringa.com/">Skaringa</a>
 * Java and XML language binding framework which provides for Java object XML serialization.</p>
 *
 * <p>Note that Skaringa has some dependencies of its own.  It requires the standard XML API's
 * (xml-apis.jar from the <a href="http://xml.apache.org/xerces2-j/">Apache Xerces2-j</a> project or j2sdk1.4+),
 * an XML Transformation engine (<a href="http://xml.apache.org/xalan-j/">Apache Xalan-j</a>),
 * and a logging api (<a href="http://jakarta.apache.org/commons/logging.html">Apache Jakarta Commons Logging</a>),
 * and, if desired, a logging implementation (<a href="http://logging.apache.org/log4j/">Apache Log4j</a> or j2sdk1.4+ logging).</p>
 *
 * <p>One more quick note.  j2sdk1.4+ comes with an old buggy version of Xalan which you should override using the
 * endorsed package override mechanism.  To do this, add your preferred version of xalan.jar to JAVA_HOME/jre/lib/endorsed.
 * You will need to create the 'endorsed' directory if it doesn't already exist.  That is the *only* way to override packages
 * that the JDK already provides in j2sdk1.4.x and above.  In JDK1.3.x, this isn't a problem, although you will need to supply
 * everything mentioned above on the classpath.</p>
 *
 * @see org.prevayler.implementation.snapshot.SnapshotManager
 * @see org.prevayler.implementation.snapshot.AbstractSnapshotManager
 * @deprecated Use {@link SkaringaSerializationStrategy} instead.
 */
public class SkaringaSnapshotManager extends GenericSnapshotManager {
	public static final String SUFFIX = "skaringasnapshot";

	public SkaringaSnapshotManager(Object newPrevalentSystem, String snapshotDirectoryName) throws ClassNotFoundException, IOException {
		super(new SkaringaSerializationStrategy(), newPrevalentSystem, snapshotDirectoryName);
	}


	/**
	 * @see org.prevayler.implementation.snapshot.AbstractSnapshotManager#suffix()
	 */
	protected String suffix() {
		return SUFFIX;
	}



	/**
	 * Find the latest snapshot file. Returns null if no snapshot file was found or if the latest snapshot
	 * does not have the suffix "skaringasnapshot".
	 *
	 * @deprecated Use {@link #latestSnapshot(java.io.File)} instead.
	 */
	public static File latestSnapshotFile(File directory) throws IOException {
		return latestSnapshotFile(directory, SUFFIX);
	}
}
