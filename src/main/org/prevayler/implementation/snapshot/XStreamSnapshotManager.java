//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Jacob Kjome.

package org.prevayler.implementation.snapshot;

import java.io.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;


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
    
	public XStreamSnapshotManager(Object newPrevalentSystem, String snapshotDirectoryName) throws ClassNotFoundException, IOException {
		super(newPrevalentSystem, snapshotDirectoryName);
		_xstream = new XStream();
	}

	public XStreamSnapshotManager(XStream xstream, Object newPrevalentSystem, String snapshotDirectoryName) throws ClassNotFoundException, IOException {
		super(newPrevalentSystem, snapshotDirectoryName);
		_xstream = xstream;
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
