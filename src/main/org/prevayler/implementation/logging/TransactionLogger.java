//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.logging;

import java.io.IOException;
import java.util.Date;

import org.prevayler.Transaction;
import org.prevayler.foundation.Turn;
import org.prevayler.implementation.publishing.TransactionSubscriber;

// START SNIPPET: transactionLogger
public interface TransactionLogger {

	public void log(Transaction transaction, Date executionTime, Turn threadSynchronizationTurn);

	public void update(TransactionSubscriber subscriber, long initialTransaction) throws IOException, ClassNotFoundException;

	public void close() throws IOException;

}
// END SNIPPET: transactionLogger