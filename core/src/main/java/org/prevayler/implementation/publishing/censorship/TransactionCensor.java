//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.publishing.censorship;

import org.prevayler.implementation.TransactionTimestamp;

// START SNIPPET: censor
public interface TransactionCensor {
	
	public void approve(TransactionTimestamp transactionTimestamp) throws RuntimeException, Error;

}
//END SNIPPET: censor
