package org.prevayler.demos.demo2.business.transactions;

import java.util.Date;

import org.prevayler.demos.demo2.business.*;


abstract class AccountTransaction extends BankTransaction {

	private final long _accountNumber;


	protected AccountTransaction(Account account) {
		_accountNumber = account.number();
	}

	protected Object executeOn(Bank bank, Date timestamp) throws Exception {
		executeOn(bank.findAccount(_accountNumber), timestamp);
		return null;
	}

	protected abstract void executeOn(Account account, Date timestamp) throws Exception;
}