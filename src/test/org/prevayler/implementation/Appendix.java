package org.prevayler.implementation;

import java.util.Date;

import org.prevayler.Transaction;


class Appendix implements Transaction {
	
	private final String appendix;
	
	public void executeOn(Object prevalentSystem, Date ignored) {
		((AppendingSystem)prevalentSystem).append(appendix);
	}

	Appendix(String appendix) {
		this.appendix = appendix;
	}
}