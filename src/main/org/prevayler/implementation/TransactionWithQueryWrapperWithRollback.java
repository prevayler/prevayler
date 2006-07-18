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

@SuppressWarnings("deprecation") @Safety(journaling = ROLLBACK_ON_RUNTIME_EXCEPTION, locking = PREVALENT_SYSTEM) public class TransactionWithQueryWrapperWithRollback<S, R, E extends Exception> implements GenericTransaction<S, R, E>, Serializable {

    private static final long serialVersionUID = 1L;

    public org.prevayler.TransactionWithQuery<S, R, E> _transactionWithQuery;

    public TransactionWithQueryWrapperWithRollback() {
    }

    public TransactionWithQueryWrapperWithRollback(org.prevayler.TransactionWithQuery<S, R, E> transactionWithQuery) {
        _transactionWithQuery = transactionWithQuery;
    }

    public R executeOn(PrevalenceContext<? extends S> prevalenceContext) throws E {
        return _transactionWithQuery.executeAndQuery(prevalenceContext.prevalentSystem(), prevalenceContext.executionTime());
    }

}
