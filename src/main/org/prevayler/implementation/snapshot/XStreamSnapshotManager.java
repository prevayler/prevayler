//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Jacob Kjome.

package org.prevayler.implementation.snapshot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.XppDriver;


/**
 * Writes and reads snapshots to/from XML files.
 *
 * <p>This implementation requires the <a href="http://xstream.codehaus.org/">XStream</a>
 * Java and XML language binding framework which provides for Java object XML serialization.</p>
 *
 * <p>Note that XStream has some dependencies of its own.  It requires the standard XML API's
 * (xml-apis.jar from the <a href="http://xml.apache.org/xerces2-j/">Apache Xerces-j</a> project or j2sdk1.4.x).</p>
 *
 * @see org.prevayler.implementation.snapshot.SnapshotManager
 * @see org.prevayler.implementation.snapshot.AbstractSnapshotManager
 */
public class XStreamSnapshotManager extends AbstractSnapshotManager {

    private XStream _xstream;
    
    /**
     * Creates a new XStreamSnapshotManager using a default XStream instance.
     * 
     * This default instance uses the XppDriver for XStream.
     * 
     * @see com.thoughtworks.xstream.io.xml.XppDriver 
     * @param newPrevalentSystem the prevalent system to snapshot.
     * @param snapshotDirectoryName the directory name where the snapshot must be stored.
     * @throws ClassNotFoundException if some class from the system cannot be found. 
     * @throws IOException if there's a problem reading the latest snapshot.
     */
	public XStreamSnapshotManager(Object newPrevalentSystem, String snapshotDirectoryName) throws ClassNotFoundException, IOException {
		this(new XStream(new XppDriver()), newPrevalentSystem, snapshotDirectoryName);
	}

	/**
	 * Creates a new XStreamSnapshotManager using a pre-configured XStream instance.
	 * 
	 * <b>Tip</b>: It's possible to achieve a substantial performance improvement using the XppDriver
	 * (com.thoughtworks.xstream.io.xml.XppDriver) with XStream.
	 * 
     * @see com.thoughtworks.xstream.io.xml.XppDriver 
	 * @param xstream a pre-configured XStream instance.
     * @param newPrevalentSystem the prevalent system to snapshot.
     * @param snapshotDirectoryName the directory name where the snapshot must be stored.
     * @throws ClassNotFoundException if some class from the system cannot be found. 
     * @throws IOException if there's a problem reading the latest snapshot.
	 */
	public XStreamSnapshotManager(XStream xstream, Object newPrevalentSystem, String snapshotDirectoryName) throws ClassNotFoundException, IOException {
		_xstream = xstream;
		init(newPrevalentSystem, snapshotDirectoryName);
	}

    /**
	 * @see org.prevayler.implementation.snapshot.SnapshotManager#writeSnapshot(Object, OutputStream)
	 */
	public void writeSnapshot(Object prevalentSystem, OutputStream out) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(out));
		try {
			_xstream.toXML(prevalentSystem, writer);
		} catch (StreamException se) {
			throw new IOException("Unable to serialize with XStream: " + se.getMessage());
		} finally {
			if (writer != null) writer.close();
		}
	}


	/**
	 * @see org.prevayler.implementation.snapshot.SnapshotManager#readSnapshot(InputStream)
	 */
	public Object readSnapshot(InputStream in) throws IOException {
        Reader reader = new BufferedReader(new InputStreamReader(in));
		try {
			return _xstream.fromXML(reader);
		} catch (StreamException se) {
			throw new IOException("Unable to deserialize with XStream: " + se.getMessage());
		} finally {
			if (reader != null) reader.close();
		}
	}


	/**
	 * @see org.prevayler.implementation.snapshot.AbstractSnapshotManager#suffix()
	 */
	protected String suffix() {
		return "xstreamsnapshot";
	}

}
