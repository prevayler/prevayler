package org.prevayler.demos.demo2.business.transactions;

import java.util.Date;

import org.prevayler.demos.demo2.business.*;


public class Withdrawal extends AccountTransaction {

	protected long _amount;

    private Withdrawal() {} //Necessary for Skaringa XML serialization
	public Withdrawal(Account account, long amount) {
		super(account);
		_amount = amount;
	}

	public void executeAndQuery(Account account, Date timestamp) throws Account.InvalidAmount {
		account.withdraw(_amount, timestamp);
	}
}
