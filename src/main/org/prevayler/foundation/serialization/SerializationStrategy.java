package org.prevayler.foundation.serialization;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

public interface SerializationStrategy {

	/**
	 * Create a Serializer to the given stream. The caller is responsible for closing
	 * the stream if necessary.
	 */
	public Serializer createSerializer(OutputStream stream) throws IOException;

	/**
	 * Create a Deserializer from the given stream. The created Deserializer is allowed to
	 * buffer the input beyond what is required for reading any particular object, so the
	 * caller cannot expect to continue reading from the stream after passing it to this
	 * method. The caller, however, is responsible for closing the stream if necessary.
	 */
	public Deserializer createDeserializer(InputStream stream) throws IOException;

	public Object deepCopy(Object original) throws IOException, ClassNotFoundException;

}
