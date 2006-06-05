package org.prevayler.implementation.journal;

import org.prevayler.PrevaylerIOException;

public class JournalException extends PrevaylerIOException {

	private static final long serialVersionUID = 1L;

	public JournalException() {
		super();
	}

	public JournalException(String message) {
		super(message);
	}

	public JournalException(String message, Throwable cause) {
		super(message, cause);
	}

	public JournalException(Throwable cause) {
		super(cause);
	}

}
