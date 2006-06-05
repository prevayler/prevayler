package org.prevayler.foundation.network;

import org.prevayler.PrevaylerIOException;

public class PortNotInServiceException extends PrevaylerIOException {

	private static final long serialVersionUID = 1L;

	public PortNotInServiceException() {
		super();
	}

	public PortNotInServiceException(String message) {
		super(message);
	}

	public PortNotInServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public PortNotInServiceException(Throwable cause) {
		super(cause);
	}

}
