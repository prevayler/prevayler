package org.prevayler.foundation;

import org.prevayler.foundation.serialization.JavaSerializationStrategy;

/**
 * @deprecated Use an appropriate SerializationStrategy instead.
 */
public class DeepCopier {

	public static Object deepCopy(Object original, String errorMessage) {
		try {
			return new JavaSerializationStrategy().deepCopy(original);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(errorMessage);
		}
	}

}
