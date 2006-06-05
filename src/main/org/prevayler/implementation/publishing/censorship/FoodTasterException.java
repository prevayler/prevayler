package org.prevayler.implementation.publishing.censorship;

import org.prevayler.PrevaylerException;

public class FoodTasterException extends PrevaylerException {

	private static final long serialVersionUID = 1L;

	public FoodTasterException() {
		super();
	}

	public FoodTasterException(String message) {
		super(message);
	}

	public FoodTasterException(String message, Throwable cause) {
		super(message, cause);
	}

	public FoodTasterException(Throwable cause) {
		super(cause);
	}

}
