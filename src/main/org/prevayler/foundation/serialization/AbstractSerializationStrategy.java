package org.prevayler.foundation.serialization;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

public abstract class AbstractSerializationStrategy implements SerializationStrategy {

	public Object deepCopy(Object original) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		Serializer objectOut = createSerializer(byteOut);
		objectOut.writeObject(original);
		objectOut.flush();
		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		Deserializer objectIn = createDeserializer(byteIn);
		return objectIn.readObject();
	}

}
