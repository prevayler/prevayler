package org.prevayler.foundation.serialization;

import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Writes and reads objects using XML. This strategy works for snapshots <b>but not for journals</b>.
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
 */
public class SkaringaSerializationStrategy extends AbstractSerializationStrategy {

	public SkaringaSerializationStrategy() {
	}

	public Serializer createSerializer(OutputStream stream) throws IOException {
		return new SkaringaSerializer(stream);
	}

	public Deserializer createDeserializer(InputStream stream) throws IOException {
		return new SkaringaDeserializer(stream);
	}

	static ObjectTransformer transformer() throws IOException {
		try {
			return ObjectTransformerFactory.getInstance().getImplementation();
//			Other options you can try:
//			trans.setProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
//			trans.setProperty(javax.xml.transform.OutputKeys.ENCODING, "ISO-8859-1");
//			trans.setProperty(com.skaringa.javaxml.PropertyKeys.OMIT_XSI_TYPE, "true");
		}
		catch (NoImplementationException nie) {
			throw new IOException("Unable to start Skaringa: " + nie.getMessage());
		}
	}

}
