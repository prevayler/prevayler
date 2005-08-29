package org.prevayler.demos.demo2.business.transactions;

import java.util.Date;

import org.prevayler.demos.demo2.business.*;


public class AccountDeletion extends BankTransaction {

	private static final long serialVersionUID = -3401288850388764433L;
	private long _accountNumber;

    private AccountDeletion() {} //Necessary for Skaringa XML serialization
	public AccountDeletion(Account account) {
		_accountNumber = account.number();
	}

	protected Object executeAndQuery(Bank bank, Date ignored) throws Bank.AccountNotFound {
		bank.deleteAccount(_accountNumber);
		return null;
	}
}