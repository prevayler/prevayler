//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.journal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.prevayler.Transaction;
import org.prevayler.foundation.Turn;
import org.prevayler.implementation.TransactionTimestamp;
import org.prevayler.implementation.publishing.TransactionSubscriber;


public class TransientJournal implements Journal {

	private final List journal = new ArrayList();
	private long _initialTransaction;
	private boolean _initialTransactionInitialized = false;


	public void append(Transaction transaction, Date executionTime, Turn myTurn) {
		if (!_initialTransactionInitialized) throw new IllegalStateException("Journal.update() has to be called at least once before Journal.journal().");

		try {
			myTurn.start();
			journal.add(new TransactionTimestamp(transaction, executionTime));
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
		if (i > journal.size()) throw new IOException("The transaction journal has not yet reached transaction " + initialTransaction + ". The last logged transaction was " + (_initialTransaction + journal.size() - 1) + ".");

		while (i != journal.size()) {
			TransactionTimestamp entry = (TransactionTimestamp)journal.get(i);
			subscriber.receive(entry.transaction(), entry.timestamp());
			i++;
		}
	}

	public void close() {}

}
