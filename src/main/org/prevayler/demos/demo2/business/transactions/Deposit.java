package org.prevayler.demos.demo2.business.transactions;

import java.util.Date;

import org.prevayler.demos.demo2.business.Account;

public class Deposit extends AccountTransaction {

	private long _amount;

    private Deposit() {} //Necessary for Skaringa XML serialization
	public Deposit(Account account, long amount) {
		super(account);
		_amount = amount;
	}

	public void executeAndQuery(Account account, Date timestamp) throws Account.InvalidAmount {
		account.deposit(_amount, timestamp);
	}
}