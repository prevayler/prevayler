package org.prevayler.foundation.serialization;

import org.prevayler.PrevaylerIOException;

public class SkaringaException extends PrevaylerIOException {

	private static final long serialVersionUID = 1L;

	public SkaringaException() {
		super();
	}

	public SkaringaException(String message) {
		super(message);
	}

	public SkaringaException(String message, Throwable cause) {
		super(message, cause);
	}

	public SkaringaException(Throwable cause) {
		super(cause);
	}

}
