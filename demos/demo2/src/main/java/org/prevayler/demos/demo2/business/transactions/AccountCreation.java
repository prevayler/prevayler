package org.prevayler.demos.demo2.business.transactions;

import java.util.Date;

import org.prevayler.demos.demo2.business.*;


public class AccountCreation extends BankTransaction {

	private static final long serialVersionUID = 476105268406333743L;
	private String _holder;

    private AccountCreation() {} //Necessary for Skaringa XML serialization
	public AccountCreation(String holder) {
		_holder = holder;
	}

	protected Object executeAndQuery(Bank bank, Date ignored) throws Account.InvalidHolder {
		return bank.createAccount(_holder);
	}

}
