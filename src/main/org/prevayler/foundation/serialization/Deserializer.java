package org.prevayler.foundation.serialization;

import java.io.IOException;

public interface Deserializer {

	/**
	 * Return the next object in the input stream, or throw EOFException if none.
	 */
	public Object readObject() throws IOException, ClassNotFoundException;

}
