package org.prevayler.foundation;

import org.prevayler.foundation.serialization.JavaSerializationStrategy;

public class DeepCopier {

	public static Object deepCopy(Object original, String errorMessage) {  //TODO Receive a generic "Serializer".
		try {
			return new JavaSerializationStrategy().deepCopy(original);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(errorMessage);
		}
	}

}
