package org.prevayler.foundation.serialization;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * A strategy for writing objects to and reading objects from streams. Implementations <b>must</b> be safe for
 * concurrent use by multiple threads.
 * <p>
 * If an implementation will be used for snapshots, it must be able to write and read the prevalent system it will
 * be used with, but does not need to be able to write or read any other objects. If an implementation will be used
 * for journals, it must be able to write and read any transactions it will be used with, but does not need to be
 * able to write or read any other objects.
 */
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
