package org.prevayler.demos.demo2.business.transactions;

import org.prevayler.demos.demo2.business.*;


abstract class AccountTransaction extends BankTransaction {

	private final long _accountNumber;


	protected AccountTransaction(Account account) {
		_accountNumber = account.number();
	}

	protected Object executeAndQuery(Bank bank) throws Exception {
		executeAndQuery(bank.findAccount(_accountNumber));
		return null;
	}

	protected abstract void executeAndQuery(Account account) throws Exception;
}