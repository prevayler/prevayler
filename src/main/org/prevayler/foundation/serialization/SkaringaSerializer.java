package org.prevayler.foundation.serialization;

import com.skaringa.javaxml.SerializerException;

import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Writes and reads objects to/from XML files.
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
 */
public class SkaringaSerializer implements Serializer {

	private StreamResult _result;

	public SkaringaSerializer(OutputStream stream) {
		_result = new StreamResult(stream);
	}

	public void writeObject(Object object) throws IOException {
		try {
			SkaringaSerializationStrategy.transformer().serialize(object, _result);
		} catch (SerializerException se) {
			throw new IOException("Unable to serialize with Skaringa: " + se.getMessage());
		}
	}

	public void flush() throws IOException {
		_result.getOutputStream().flush();
	}

}
