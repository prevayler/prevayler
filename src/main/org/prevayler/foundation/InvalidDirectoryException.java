package org.prevayler.foundation;

import org.prevayler.PrevaylerIOException;

public class InvalidDirectoryException extends PrevaylerIOException {

	private static final long serialVersionUID = 1L;

	public InvalidDirectoryException() {
		super();
	}

	public InvalidDirectoryException(String message) {
		super(message);
	}

	public InvalidDirectoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidDirectoryException(Throwable cause) {
		super(cause);
	}

}
