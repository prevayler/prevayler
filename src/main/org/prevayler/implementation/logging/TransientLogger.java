//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.logging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.prevayler.Transaction;
import org.prevayler.foundation.Turn;
import org.prevayler.implementation.*;
import org.prevayler.implementation.publishing.TransactionSubscriber;


public class TransientLogger implements TransactionLogger {

	private final List log = new ArrayList();
	private long _initialTransaction;
	private boolean _initialTransactionInitialized = false;


	public void log(Transaction transaction, Date executionTime, Turn myTurn) {
		if (!_initialTransactionInitialized) throw new IllegalStateException("TransactionLogger.update() has to be called at least once before TransactionLogger.log().");

		try {
			myTurn.start();
			log.add(new TransactionTimestamp(transaction, executionTime));
		} finally {
			myTurn.end();
		}
	}

	public synchronized void update(TransactionSubscriber subscriber, long initialTransaction) throws IOException {
		if (!_initialTransactionInitialized) {
			_initialTransactionInitialized = true;
			_initialTransaction = initialTransaction;
			return;
		}
		if (initialTransaction < _initialTransaction) throw new IOException("Unable to recover transaction " + initialTransaction + ". The oldest recoverable transaction is " + _initialTransaction + ".");

		int i = (int)(initialTransaction - _initialTransaction);
		if (i > log.size()) throw new IOException("The transaction log has not yet reached transaction " + initialTransaction + ". The last logged transaction was " + (_initialTransaction + log.size() - 1) + ".");

		while (i != log.size()) {
			TransactionTimestamp entry = (TransactionTimestamp)log.get(i);
			subscriber.receive(entry.transaction(), entry.timestamp());
			i++;
		}
	}

	public void close() {}

}
