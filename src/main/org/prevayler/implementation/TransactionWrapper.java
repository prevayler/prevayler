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

import static org.prevayler.Safety.Journaling.ROLLBACK_ON_RUNTIME_EXCEPTION;
import static org.prevayler.Safety.Locking.PREVALENT_SYSTEM;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.Safety;

import java.io.Serializable;

@SuppressWarnings("deprecation") @Safety(journaling = ROLLBACK_ON_RUNTIME_EXCEPTION, locking = PREVALENT_SYSTEM) public class TransactionWrapper<S> implements GenericTransaction<S, Void, RuntimeException>, Serializable {

    private static final long serialVersionUID = 1L;

    public org.prevayler.Transaction<S> _transaction;

    public TransactionWrapper() {
    }

    public TransactionWrapper(org.prevayler.Transaction<S> transaction) {
        _transaction = transaction;
    }

    public Void executeOn(S prevalentSystem, PrevalenceContext prevalenceContext) {
        _transaction.executeOn(prevalentSystem, prevalenceContext.executionTime());
        return null;
    }

}
