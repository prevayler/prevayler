package org.prevayler.implementation.snapshot;

import org.prevayler.PrevaylerIOException;

public class SnapshotException extends PrevaylerIOException {

	private static final long serialVersionUID = 1L;

	public SnapshotException() {
		super();
	}

	public SnapshotException(String message) {
		super(message);
	}

	public SnapshotException(String message, Throwable cause) {
		super(message, cause);
	}

	public SnapshotException(Throwable cause) {
		super(cause);
	}

}
