// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.demo2.business.transactions;

import org.prevayler.TransactionWithQuery;
import org.prevayler.demos.demo2.business.Bank;

import java.util.Date;

public abstract class BankTransaction implements TransactionWithQuery {

    public Object executeAndQuery(Object bank, Date timestamp) throws Exception {
        return executeAndQuery((Bank) bank, timestamp);
    }

    protected abstract Object executeAndQuery(Bank bank, Date timestamp) throws Exception;
}
