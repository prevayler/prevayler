package org.prevayler.foundation.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JavaSerializationStrategy extends AbstractSerializationStrategy {

	private final ClassLoader _loader;

	public JavaSerializationStrategy() {
		_loader = null;
	}

	public JavaSerializationStrategy(ClassLoader loader) {
		_loader = loader;
	}

	public Serializer createSerializer(OutputStream stream) throws IOException {
		return new JavaSerializer(stream);
	}

	public Deserializer createDeserializer(InputStream stream) throws IOException {
		return new JavaDeserializer(stream, _loader);
	}

}
