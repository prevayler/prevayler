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

class TransactionCapsule extends Capsule {

    private static final long serialVersionUID = 3283271592697928351L;

    public TransactionCapsule(Transaction transaction, Serializer journalSerializer) {
        super(transaction, journalSerializer);
    }

    public TransactionCapsule(byte[] serialized) {
        super(serialized);
    }

    protected void execute(Object transaction, Object prevalentSystem, Date executionTime) {
        ((Transaction) transaction).executeOn(prevalentSystem, executionTime);
    }

    public Capsule cleanCopy() {
        // TransactionCapsule, unlike TransactionWithQueryCapsule, is completely
        // immutable.
        return this;
    }

}
