package org.prevayler.foundation;

import java.io.IOException;
import java.io.OutputStream;

public abstract class Guided {

	private final Turn _turn;

	protected Guided(Turn turn) {
		_turn = turn;
	}

	public void startTurn() {
		_turn.start();
	}

	public void endTurn() {
		_turn.end();
	}

	public abstract void writeTo(OutputStream stream) throws IOException;

}
