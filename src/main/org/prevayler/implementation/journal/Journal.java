//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.journal;

import org.prevayler.Transaction;
import org.prevayler.foundation.Turn;
import org.prevayler.implementation.publishing.TransactionSubscriber;

import java.io.IOException;
import java.util.Date;

// START SNIPPET: journal
 
public interface Journal {

	public void append(Transaction transaction, Date executionTime, Turn threadSynchronizationTurn);

	public void update(TransactionSubscriber subscriber, long initialTransaction) throws IOException, ClassNotFoundException;

	public void close() throws IOException;

	public long nextTransaction();

}
// END SNIPPET: journal
