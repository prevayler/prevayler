package org.prevayler.implementation;

import java.util.Date;

public class NondeterministicErrorTransaction extends Appendix {

	private static final long serialVersionUID = 1L;

    private static int _timeToDetonation = 0;

	public static synchronized void armBomb(int timeToDetonation) {
		_timeToDetonation = timeToDetonation;
	}

	private static synchronized void triggerBomb() {
		if (_timeToDetonation > 0 && --_timeToDetonation == 0) {
			throw new Error("BOOM!");
		}
	}

	public NondeterministicErrorTransaction(String toAdd) {
		super(toAdd);
	}

	public void executeOn(Object prevalentSystem, Date executionTime) {
		triggerBomb();
		super.executeOn(prevalentSystem, executionTime);
	}

}
