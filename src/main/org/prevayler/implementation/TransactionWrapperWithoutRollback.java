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

import static org.prevayler.Safety.Journaling.*;
import static org.prevayler.Safety.Locking.*;

import org.prevayler.*;

import java.io.*;

@SuppressWarnings("deprecation") @Safety(journaling = DESERIALIZE_BEFORE_EXECUTION, locking = PREVALENT_SYSTEM) public class TransactionWrapperWithoutRollback<S> implements GenericTransaction<S, Void, RuntimeException>, Serializable {

    private static final long serialVersionUID = 1L;

    public org.prevayler.Transaction<S> _transaction;

    public TransactionWrapperWithoutRollback() {
    }

    public TransactionWrapperWithoutRollback(org.prevayler.Transaction<S> transaction) {
        _transaction = transaction;
    }

    public Void executeOn(PrevalenceContext<? extends S> prevalenceContext) {
        _transaction.executeOn(prevalenceContext.prevalentSystem(), prevalenceContext.executionTime());
        return null;
    }

}
