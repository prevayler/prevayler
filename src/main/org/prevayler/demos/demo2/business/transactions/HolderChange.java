package org.prevayler.demos.demo2.business.transactions;

import org.prevayler.demos.demo2.business.Account;

public class HolderChange extends AccountTransaction {

	private final String _newHolder;


	public HolderChange(Account account, String newHolder) {
		super(account);
		_newHolder = newHolder;
	}

	public void executeAndQuery(Account account) throws Account.InvalidHolder {
		account.holder(_newHolder);
	}
}