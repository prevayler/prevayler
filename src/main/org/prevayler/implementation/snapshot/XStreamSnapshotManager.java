//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Jacob Kjome.

package org.prevayler.implementation.snapshot;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.thoughtworks.xstream.XStream;


/**
 * Writes and reads snapshots to/from XML files.
 *
 * <p>This implementation requires the <a href="http://xstream.codehaus.org/">XStream</a>
 * Java and XML language binding framework which provides for Java object XML serialization.</p>
 *
 * <p>Note that XStream has some dependencies of its own.  It requires the standard XML API's
 * (xml-apis.jar from the <a href="http://xml.apache.org/xerces2-j/">Apache Xerces2-j</a> project or j2sdk1.4+)
 * and an XML implementation (again, provided by Xerces2 or j2sdk1.4+).</p>
 *
 * <p>To make XStream up to 10x faster, add <a href="http://www.extreme.indiana.edu/xgws/xsoap/xpp/mxp1/">XPP3</a>
 * to the classpath. XStream has the concept of a
 * <a href="http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/HierarchicalStreamDriver.html">HierarchicalStreamDriver</a>
 * and the default implementation for XStream is the highly performant XppDriver.  However, XStream will fall back to the DomDriver if XPP3 is
 * not found in the classpath making the XPP3 library entirely optional... well, not quite.  See <a href="http://jira.codehaus.org/browse/XSTR-71">XSTR-71</a>.
 * The current decision in that issue forces XPP3 to be a required runtime dependency when using XStream unless one specifically configures another driver, such as
 * the DomDriver.</p>
 *
 * @see org.prevayler.implementation.snapshot.SnapshotManager
 */
public class XStreamSnapshotManager extends SnapshotManager {

    private ThreadLocal _xstreams = new ThreadLocal() {
        protected Object initialValue() {
            return createXStream();
        }
    };

    private String _encoding;

    /**
     * @see #XStreamSnapshotManager(Object, String, String)
     */
    public XStreamSnapshotManager(Object newPrevalentSystem, String snapshotDirectoryName) throws ClassNotFoundException, IOException {
        this(newPrevalentSystem, snapshotDirectoryName, null);
    }

    /**
     * Creates a new XStreamSnapshotManager
     * 
     * @param newPrevalentSystem the prevalent system to snapshot.
     * @param snapshotDirectoryName the directory name where the snapshot must be stored.
     * @param encoding optionally specify the character encoding for XML serialization.  May be null.
     * @throws ClassNotFoundException if some class from the system cannot be found. 
     * @throws IOException if there's a problem reading the latest snapshot.
     */
    public XStreamSnapshotManager(Object newPrevalentSystem, String snapshotDirectoryName, String encoding) throws ClassNotFoundException, IOException {
        _encoding = encoding;
        init(newPrevalentSystem, snapshotDirectoryName);
    }

    private XStream getXStream() {
        return (XStream) _xstreams.get();
    }

    /**
     * @see org.prevayler.implementation.snapshot.SnapshotManager#writeSnapshot(Object, OutputStream)
     */
    public void writeSnapshot(Object prevalentSystem, OutputStream out) throws IOException {
        OutputStreamWriter writer = _encoding == null ? new OutputStreamWriter(out) : new OutputStreamWriter(out, _encoding);
        getXStream().toXML(prevalentSystem, writer);
        writer.flush();
    }

    /**
	 * @see org.prevayler.implementation.snapshot.SnapshotManager#readSnapshot(InputStream)
     */
    public Object readSnapshot(InputStream in) throws IOException {
        return getXStream().fromXML(_encoding == null ? new InputStreamReader(in) : new InputStreamReader(in, _encoding));
    }

    /**
     * @see org.prevayler.implementation.snapshot.SnapshotManager#suffix()
     */
    protected String suffix() {
        return "xstreamsnapshot";
    }

    /**
     * Create a new XStream instance. This must be a new instance because XStream instances are not threadsafe.
     */
    protected XStream createXStream() {
        return new XStream();
    }

}