package org.prevayler.foundation.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.thoughtworks.xstream.XStream;


/**
 * Writes and reads objects to/from XML streams.
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
 * not found in the classpath making the XPP3 library entirely optional.</p>
 *
 * @see org.prevayler.implementation.snapshot.SnapshotManager
 * @see org.prevayler.implementation.snapshot.AbstractSnapshotManager
 */
public class XStreamSerializationStrategy extends AbstractSerializationStrategy {

	private XStream _xstream;

	public XStreamSerializationStrategy() {
		this(new XStream());
	}

	public XStreamSerializationStrategy(XStream xstream) {
		_xstream = xstream;
	}

	public Serializer createSerializer(OutputStream stream) throws IOException {
		return new XStreamSerializer(_xstream, stream);
	}

	public Deserializer createDeserializer(InputStream stream) throws IOException {
		return new XStreamDeserializer(_xstream, stream);
	}

}
