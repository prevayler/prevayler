package org.prevayler;

import java.io.IOException;

public class PrevaylerIOException extends IOException {

	private static final long serialVersionUID = 1L;

	public PrevaylerIOException() {
		super();
	}

	public PrevaylerIOException(String message) {
		super(message);
	}

	public PrevaylerIOException(String message, Throwable cause) {
		super(message);
		initCause(cause);
	}

	public PrevaylerIOException(Throwable cause) {
		super();
		initCause(cause);
	}

}
