package org.prevayler.demos.demo2.business.transactions;

import java.util.Date;

import org.prevayler.demos.demo2.business.Account;

public class HolderChange extends AccountTransaction {

	private final String _newHolder;


	public HolderChange(Account account, String newHolder) {
		super(account);
		_newHolder = newHolder;
	}

	public void executeAndQuery(Account account, Date ignored) throws Account.InvalidHolder {
		account.holder(_newHolder);
	}
}