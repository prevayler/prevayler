package org.prevayler.demos.demo2.business.transactions;

import org.prevayler.demos.demo2.business.Bank;
import org.prevayler.util.TransactionWithQuery;

public abstract class BankTransaction extends TransactionWithQuery {

	public Object executeAndQuery(Object bank) throws Exception {
		return executeAndQuery((Bank)bank);
	}

	protected abstract Object executeAndQuery(Bank bank) throws Exception;
}
