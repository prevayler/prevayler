package org.prevayler.foundation.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GZIPSerializationStrategy extends AbstractSerializationStrategy {

	private SerializationStrategy _underlying;

	public GZIPSerializationStrategy(SerializationStrategy underlying) {
		_underlying = underlying;
	}

	public Serializer createSerializer(OutputStream stream) throws IOException {
		return _underlying.createSerializer(new MultiMemberGZIPOutputStream(stream));
	}

	public Deserializer createDeserializer(InputStream stream) throws IOException {
		return _underlying.createDeserializer(new MultiMemberGZIPInputStream(stream));
	}

}
