package org.prevayler.foundation.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Writes and reads objects using Java serialization. This strategy can be used for snapshots, journals or both.
 */
public class JavaSerializationStrategy extends AbstractSerializationStrategy {

	private final ClassLoader _loader;

	public JavaSerializationStrategy() {
		this(null);
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
