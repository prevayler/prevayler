package org.prevayler.demos.demo2.business.transactions;

import java.util.Date;

import org.prevayler.demos.demo2.business.*;


public class AccountDeletion extends BankTransaction {

	private final long _accountNumber;

	public AccountDeletion(Account account) {
		_accountNumber = account.number();
	}

	protected Object executeAndQuery(Bank bank, Date ignored) throws Bank.AccountNotFound {
		bank.deleteAccount(_accountNumber);
		return null;
	}
}