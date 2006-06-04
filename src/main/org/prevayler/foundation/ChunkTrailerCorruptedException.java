package org.prevayler.foundation;

import org.prevayler.PrevaylerIOException;

public class ChunkTrailerCorruptedException extends PrevaylerIOException {

	private static final long serialVersionUID = 1L;

	public ChunkTrailerCorruptedException() {
		super();
	}

	public ChunkTrailerCorruptedException(String message) {
		super(message);
	}

	public ChunkTrailerCorruptedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ChunkTrailerCorruptedException(Throwable cause) {
		super(cause);
	}

}
