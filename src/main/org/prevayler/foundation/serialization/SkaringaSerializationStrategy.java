package org.prevayler.foundation.serialization;

import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
