//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

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