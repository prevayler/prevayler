package org.prevayler.foundation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DeepCopier {

	public static Object deepCopy(Object original, String errorMessage) {  //TODO Receive a generic "Serializer".
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
			objectOut.writeObject(original);
			objectOut.close();
			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			ObjectInputStream objectIn = new ObjectInputStream(byteIn);
			return objectIn.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(errorMessage);
		}
	}

}
