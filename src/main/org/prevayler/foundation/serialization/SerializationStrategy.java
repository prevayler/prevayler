package org.prevayler.foundation.serialization;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

public interface SerializationStrategy {

	public Serializer createSerializer(OutputStream stream) throws IOException;

	public Deserializer createDeserializer(InputStream stream) throws IOException;

	public Object deepCopy(Object original) throws IOException, ClassNotFoundException;

}
