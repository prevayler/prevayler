package org.prevayler.foundation;

import org.prevayler.PrevaylerIOException;

public class AlreadyLockedException extends PrevaylerIOException {

	private static final long serialVersionUID = 1L;

	public AlreadyLockedException() {
		super();
	}

	public AlreadyLockedException(String message) {
		super(message);
	}

	public AlreadyLockedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlreadyLockedException(Throwable cause) {
		super(cause);
	}

}
