package org.prevayler.foundation;

import org.prevayler.PrevaylerIOException;

public class AlreadyClosedException extends PrevaylerIOException {

	private static final long serialVersionUID = 1L;

	public AlreadyClosedException() {
		super();
	}

	public AlreadyClosedException(String message) {
		super(message);
	}

	public AlreadyClosedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlreadyClosedException(Throwable cause) {
		super(cause);
	}

}
