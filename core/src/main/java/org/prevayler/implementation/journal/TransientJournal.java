//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.journal;

import org.prevayler.implementation.TransactionGuide;
import org.prevayler.implementation.TransactionTimestamp;
import org.prevayler.implementation.publishing.TransactionSubscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TransientJournal implements Journal {

	private final List journal = new ArrayList();
	private long _initialTransaction;
	private boolean _initialTransactionInitialized = false;

	public void append(TransactionGuide guide) {
		if (!_initialTransactionInitialized) throw new IllegalStateException("Journal.update() has to be called at least once before Journal.journal().");

		guide.startTurn();
		try {
			guide.checkSystemVersion(_initialTransaction + journal.size());
			journal.add(guide.timestamp().cleanCopy());
		} finally {
			guide.endTurn();
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
			long recoveringTransaction = _initialTransaction + i;
			if (entry.systemVersion() != recoveringTransaction) {
				throw new IOException("Expected " + recoveringTransaction + " but was " + entry.systemVersion());
			}
			subscriber.receive(entry);
			i++;
		}
	}

	public void close() {}

	public synchronized long nextTransaction() {
		if (!_initialTransactionInitialized) throw new IllegalStateException("update() must be called at least once");
		return _initialTransaction + journal.size();
	}

}
