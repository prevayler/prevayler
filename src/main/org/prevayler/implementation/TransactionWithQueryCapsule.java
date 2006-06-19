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

class TransactionWithQueryCapsule extends Capsule {

    private static final long serialVersionUID = 78811627002206298L;

    private transient Object _queryResult;

    private transient Exception _queryException;

    public TransactionWithQueryCapsule(TransactionWithQuery transactionWithQuery, Serializer journalSerializer) {
        super(transactionWithQuery, journalSerializer);
    }

    public TransactionWithQueryCapsule(byte[] serialized) {
        super(serialized);
    }

    protected void execute(Object transaction, Object prevalentSystem, Date executionTime) {
        try {
            _queryResult = ((TransactionWithQuery) transaction).executeAndQuery(prevalentSystem, executionTime);
        } catch (RuntimeException rx) {
            _queryException = rx;
            throw rx; // This is necessary because of the rollback feature.
        } catch (Exception ex) {
            _queryException = ex;
        }
    }

    public Object result() throws Exception {
        if (_queryException != null)
            throw _queryException;
        return _queryResult;
    }

    public Capsule cleanCopy() {
        return new TransactionWithQueryCapsule(serialized());
    }

}
