package org.prevayler.foundation.serialization;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

public interface Serializer {

	/**
	 * Write an object to a stream. An implementation must ensure that the object is written
	 * completely before returning. An implementation is free to flush or close the given stream
	 * as it sees fit, but is not required to do either. An implementation can expect that the
	 * stream is already buffered, so additional buffering is not required for performance.
	 */
	public void writeObject(OutputStream stream, Object object) throws IOException;

	/**
	 * Read an object from a stream. An implementation is free to close the given stream
	 * as it sees fit, but is not required to do so. An implementation can expect that the
	 * stream is already buffered, so additional buffering is not required for performance.
	 */
	public Object readObject(InputStream stream) throws IOException, ClassNotFoundException;

}
