package org.prevayler.foundation;

import org.prevayler.PrevaylerException;

public class DeepCopyFailedException extends PrevaylerException {

	private static final long serialVersionUID = 1L;

	public DeepCopyFailedException() {
		super();
	}

	public DeepCopyFailedException(String message) {
		super(message);
	}

	public DeepCopyFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public DeepCopyFailedException(Throwable cause) {
		super(cause);
	}

}
