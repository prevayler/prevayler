package org.prevayler.foundation.serialization;

import com.skaringa.javaxml.SerializerException;

import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;

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
