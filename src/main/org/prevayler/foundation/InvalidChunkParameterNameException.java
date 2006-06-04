package org.prevayler.foundation;

import org.prevayler.PrevaylerIOException;

public class InvalidChunkParameterNameException extends PrevaylerIOException {

	private static final long serialVersionUID = 1L;

	public InvalidChunkParameterNameException() {
		super();
	}

	public InvalidChunkParameterNameException(String message) {
		super(message);
	}

	public InvalidChunkParameterNameException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidChunkParameterNameException(Throwable cause) {
		super(cause);
	}

}
