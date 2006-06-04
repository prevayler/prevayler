package org.prevayler.foundation;

import org.prevayler.PrevaylerIOException;

public class InvalidChunkParameterValueException extends PrevaylerIOException {

	private static final long serialVersionUID = 1L;

	public InvalidChunkParameterValueException() {
		super();
	}

	public InvalidChunkParameterValueException(String message) {
		super(message);
	}

	public InvalidChunkParameterValueException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidChunkParameterValueException(Throwable cause) {
		super(cause);
	}

}
