package org.prevayler.foundation.network;

import org.prevayler.PrevaylerIOException;

public class PortInServiceException extends PrevaylerIOException {

	private static final long serialVersionUID = 1L;

	public PortInServiceException() {
		super();
	}

	public PortInServiceException(String message) {
		super(message);
	}

	public PortInServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public PortInServiceException(Throwable cause) {
		super(cause);
	}

}
