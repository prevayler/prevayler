package org.prevayler.demos.demo2.business.transactions;

import java.util.Date;

import org.prevayler.demos.demo2.business.Bank;
import org.prevayler.util.TransactionWithQuery;

public abstract class BankTransaction implements TransactionWithQuery {

	public Object executeOn(Object bank, Date timestamp) throws Exception {
		return executeOn((Bank)bank, timestamp);
	}

	protected abstract Object executeOn(Bank bank, Date timestamp) throws Exception;
}
