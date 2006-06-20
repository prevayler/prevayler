// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.foundation.serialization;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A strategy for writing objects to and reading objects from streams.
 * Implementations <b>must</b> be safe for concurrent use by multiple threads.
 * <p>
 * If an implementation will be used for snapshots, it must be able to write and
 * read the prevalent system it will be used with, but does not need to be able
 * to write or read any other objects. If an implementation will be used for
 * journals, it must be able to write and read any transactions it will be used
 * with, but does not need to be able to write or read any other objects.
 */
public interface Serializer<T> {

    /**
     * Write an object to a stream. An implementation must ensure that the
     * object is written completely before returning. An implementation is free
     * to flush or close the given stream as it sees fit, but is not required to
     * do either. An implementation can expect that the stream is already
     * buffered, so additional buffering is not required for performance.
     */
    public void writeObject(OutputStream stream, T object) throws Exception;

    /**
     * Read an object from a stream. An implementation is free to close the
     * given stream as it sees fit, but is not required to do so. An
     * implementation can expect that the stream is already buffered, so
     * additional buffering is not required for performance.
     */
    public T readObject(InputStream stream) throws Exception;

}
