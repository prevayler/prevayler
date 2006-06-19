// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.foundation.serialization;

import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Writes and reads objects using XML. This serializer can be used for
 * snapshots, journals or both.
 * 
 * <p>
 * This implementation requires the <a href="http://www.skaringa.com/">Skaringa</a>
 * Java and XML language binding framework which provides for Java object XML
 * serialization.
 * </p>
 * 
 * <p>
 * Note that Skaringa has some dependencies of its own. It requires the standard
 * XML API's (xml-apis.jar from the <a
 * href="http://xml.apache.org/xerces2-j/">Apache Xerces2-j</a> project or
 * j2sdk1.4+), an XML Transformation engine (<a
 * href="http://xml.apache.org/xalan-j/">Apache Xalan-j</a>), and a logging api (<a
 * href="http://jakarta.apache.org/commons/logging.html">Apache Jakarta Commons
 * Logging</a>), and, if desired, a logging implementation (<a
 * href="http://logging.apache.org/log4j/">Apache Log4j</a> or j2sdk1.4+
 * logging).
 * </p>
 * 
 * <p>
 * One more quick note. j2sdk1.4+ comes with an old buggy version of Xalan which
 * you should override using the endorsed package override mechanism. To do
 * this, add your preferred version of xalan.jar to JAVA_HOME/jre/lib/endorsed.
 * You will need to create the 'endorsed' directory if it doesn't already exist.
 * That is the *only* way to override packages that the JDK already provides in
 * j2sdk1.4.x and above. In JDK1.3.x, this isn't a problem, although you will
 * need to supply everything mentioned above on the classpath.
 * </p>
 */
public class SkaringaSerializer implements Serializer {

    public void writeObject(OutputStream stream, Object object) throws Exception {
        createTransformer().serialize(object, new StreamResult(stream));
    }

    public Object readObject(InputStream stream) throws Exception {
        return createTransformer().deserialize(new StreamSource(stream));
    }

    protected ObjectTransformer createTransformer() throws Exception {
        return ObjectTransformerFactory.getInstance().getImplementation();
        // Other options you can try:
        // trans.setProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        // trans.setProperty(javax.xml.transform.OutputKeys.ENCODING,
        // "ISO-8859-1");
        // trans.setProperty(com.skaringa.javaxml.PropertyKeys.OMIT_XSI_TYPE,
        // "true");
    }

}
