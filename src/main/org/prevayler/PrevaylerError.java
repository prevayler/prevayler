package org.prevayler;

public class PrevaylerError extends Error {

	private static final long serialVersionUID = 1L;

	public PrevaylerError() {
		super();
	}

	public PrevaylerError(String message) {
		super(message);
	}

	public PrevaylerError(String message, Throwable cause) {
		super(message, cause);
	}

	public PrevaylerError(Throwable cause) {
		super(cause);
	}

}
