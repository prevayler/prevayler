// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation.journal;

import org.prevayler.implementation.TransactionGuide;
import org.prevayler.implementation.TransactionTimestamp;
import org.prevayler.implementation.publishing.TransactionSubscriber;

import java.util.ArrayList;
import java.util.List;

public class TransientJournal<T> implements Journal<T> {

    private final List<TransactionTimestamp<?, T>> journal = new ArrayList<TransactionTimestamp<?, T>>();

    private long _initialTransaction;

    private boolean _initialTransactionInitialized = false;

    public <X> void append(TransactionGuide<X, T> guide) {
        if (!_initialTransactionInitialized)
            throw new IllegalStateException("Journal.update() has to be called at least once before Journal.journal().");

        guide.startTurn();
        try {
            guide.checkSystemVersion(_initialTransaction + journal.size());
            journal.add(guide.timestamp().cleanCopy());
        } finally {
            guide.endTurn();
        }
    }

    public synchronized void update(TransactionSubscriber<T> subscriber, long initialTransaction) {
        if (!_initialTransactionInitialized) {
            _initialTransactionInitialized = true;
            _initialTransaction = initialTransaction;
            return;
        }

        if (initialTransaction < _initialTransaction) {
            throw new JournalError("Unable to recover transaction " + initialTransaction + ". The oldest recoverable transaction is " + _initialTransaction + ".");
        }

        int i = (int) (initialTransaction - _initialTransaction);
        if (i > journal.size()) {
            throw new JournalError("The transaction journal has not yet reached transaction " + initialTransaction + ". The last logged transaction was " + (_initialTransaction + journal.size() - 1) + ".");
        }

        while (i != journal.size()) {
            TransactionTimestamp<?, T> entry = journal.get(i);
            long recoveringTransaction = _initialTransaction + i;
            if (entry.systemVersion() != recoveringTransaction) {
                throw new JournalError("Expected " + recoveringTransaction + " but was " + entry.systemVersion());
            }
            subscriber.receive(entry);
            i++;
        }
    }

    public void close() {
    }

    public synchronized long nextTransaction() {
        if (!_initialTransactionInitialized)
            throw new IllegalStateException("update() must be called at least once");
        return _initialTransaction + journal.size();
    }

}
