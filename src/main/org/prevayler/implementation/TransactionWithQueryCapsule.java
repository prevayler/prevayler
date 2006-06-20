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

import org.prevayler.TransactionWithQuery;
import org.prevayler.foundation.serialization.Serializer;

import java.util.Date;

class TransactionWithQueryCapsule<T, R, E extends Exception> extends Capsule<TransactionWithQuery<T, R, E>, T> {

    private static final long serialVersionUID = 78811627002206298L;

    private transient R _queryResult;

    private transient RuntimeException _queryRuntimeException;

    private transient E _queryException;

    public TransactionWithQueryCapsule(TransactionWithQuery<T, R, E> transactionWithQuery, Serializer journalSerializer) {
        super(transactionWithQuery, journalSerializer);
    }

    public TransactionWithQueryCapsule(byte[] serialized) {
        super(serialized);
    }

    @SuppressWarnings("unchecked") @Override protected void execute(TransactionWithQuery<T, R, E> transaction, T prevalentSystem, Date executionTime) {
        try {
            _queryResult = transaction.executeAndQuery(prevalentSystem, executionTime);
        } catch (RuntimeException rx) {
            _queryRuntimeException = rx;
            throw rx; // This is necessary because of the rollback feature.
        } catch (Exception ex) {
            _queryException = (E) ex;
        }
    }

    public R result() throws E {
        if (_queryException != null) {
            throw _queryException;
        } else if (_queryRuntimeException != null) {
            throw _queryRuntimeException;
        } else {
            return _queryResult;
        }
    }

    @Override public TransactionWithQueryCapsule<T, R, E> cleanCopy() {
        return new TransactionWithQueryCapsule<T, R, E>(serialized());
    }

}
