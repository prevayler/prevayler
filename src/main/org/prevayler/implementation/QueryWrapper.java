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

import static org.prevayler.Safety.Journaling.TRANSIENT;
import static org.prevayler.Safety.Locking.PREVALENT_SYSTEM;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.Safety;

import java.io.Serializable;

@SuppressWarnings("deprecation") @Safety(journaling = TRANSIENT, locking = PREVALENT_SYSTEM) public class QueryWrapper<S, R, E extends Exception> implements GenericTransaction<S, R, E>, Serializable {

    private static final long serialVersionUID = 1L;

    public org.prevayler.Query<S, R, E> _sensitiveQuery;

    public QueryWrapper() {
    }

    public QueryWrapper(org.prevayler.Query<S, R, E> sensitiveQuery) {
        _sensitiveQuery = sensitiveQuery;
    }

    public R executeOn(S prevalentSystem, PrevalenceContext prevalenceContext) throws E {
        return _sensitiveQuery.query(prevalentSystem, prevalenceContext.executionTime());
    }

}
