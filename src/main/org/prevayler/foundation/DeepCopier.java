package org.prevayler.foundation;

import org.prevayler.foundation.serialization.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DeepCopier {

	public static Object deepCopy(Object original, Serializer serializer) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		serializer.writeObject(byteOut, original);
		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		return serializer.readObject(byteIn);
	}

}
