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
