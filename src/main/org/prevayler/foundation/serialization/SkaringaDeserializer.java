package org.prevayler.foundation.serialization;

import com.skaringa.javaxml.DeserializerException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

public class SkaringaDeserializer implements Deserializer {

	private StreamSource _source;

	public SkaringaDeserializer(InputStream stream) {
		_source = new StreamSource(stream);
	}

	public Object readObject() throws IOException {
		try {
			return SkaringaSerializationStrategy.transformer().deserialize(_source);
		} catch (DeserializerException de) {
			throw new IOException("Unable to deserialize with Skaringa: " + de.getMessage());
		}
	}

}
