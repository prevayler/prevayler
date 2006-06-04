package org.prevayler.foundation;

import org.prevayler.PrevaylerException;

public class UnexpectedException extends PrevaylerException {

	private static final long serialVersionUID = 1L;

	public UnexpectedException() {
		super();
	}

	public UnexpectedException(String message) {
		super(message);
	}

	public UnexpectedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnexpectedException(Throwable cause) {
		super(cause);
	}

}
