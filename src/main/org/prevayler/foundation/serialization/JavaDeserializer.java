package org.prevayler.foundation.serialization;

import org.prevayler.foundation.ObjectInputStreamWithClassLoader;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

public class JavaDeserializer implements Deserializer {

	private ObjectInputStream _stream;

	public JavaDeserializer(InputStream stream, ClassLoader loader) throws IOException {
		_stream = new ObjectInputStreamWithClassLoader(stream, loader);
	}

	public Object readObject() throws IOException, ClassNotFoundException {
		return _stream.readObject();
	}

}
