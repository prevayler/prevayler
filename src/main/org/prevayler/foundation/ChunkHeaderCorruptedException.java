package org.prevayler.foundation;

import org.prevayler.PrevaylerIOException;

public class ChunkHeaderCorruptedException extends PrevaylerIOException {

	private static final long serialVersionUID = 1L;

	public ChunkHeaderCorruptedException() {
		super();
	}

	public ChunkHeaderCorruptedException(String message) {
		super(message);
	}

	public ChunkHeaderCorruptedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ChunkHeaderCorruptedException(Throwable cause) {
		super(cause);
	}

}
