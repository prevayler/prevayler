// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation;

import org.prevayler.Transaction;
import org.prevayler.foundation.serialization.Serializer;

import java.util.Date;

class TransactionCapsule<T> extends Capsule<Transaction<? super T>, T> {

    private static final long serialVersionUID = 3283271592697928351L;

    public TransactionCapsule(Transaction<? super T> transaction, Serializer<? super Transaction<? super T>> journalSerializer) {
        super(transaction, journalSerializer);
    }

    public TransactionCapsule(byte[] serialized) {
        super(serialized);
    }

    @Override protected void execute(Transaction<? super T> transaction, T prevalentSystem, Date executionTime) {
        transaction.executeOn(prevalentSystem, executionTime);
    }

    @Override public TransactionCapsule<T> cleanCopy() {
        // TransactionCapsule, unlike TransactionWithQueryCapsule, is completely
        // immutable.
        return this;
    }

}
