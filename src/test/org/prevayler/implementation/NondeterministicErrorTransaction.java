package org.prevayler.implementation;

import java.util.Date;

public class NondeterministicErrorTransaction extends Appendix {

	private static final long serialVersionUID = 1L;

	private static boolean bombArmed = false;

	public static synchronized void armBomb() {
		bombArmed = true;
	}

	private static synchronized void triggerBomb() {
		if (bombArmed) {
			bombArmed = false;
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
