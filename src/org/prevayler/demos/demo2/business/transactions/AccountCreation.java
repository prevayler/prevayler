package org.prevayler.demos.demo2.business.transactions;

import org.prevayler.demos.demo2.business.*;


public class AccountCreation extends BankTransaction {

	private final String _holder;

	public AccountCreation(String holder) {
		_holder = holder;
	}

	protected Object executeAndQuery(Bank bank) throws Account.InvalidHolder {
		return bank.createAccount(_holder);
	}
}
