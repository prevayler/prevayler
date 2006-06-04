package org.prevayler.implementation;

public class Bomb extends Error {

	private static final long serialVersionUID = 1L;

	public Bomb() {
		super();
	}

	public Bomb(String message) {
		super(message);
	}

	public Bomb(String message, Throwable cause) {
		super(message, cause);
	}

	public Bomb(Throwable cause) {
		super(cause);
	}

}
