package org.prevayler.demos.demo2.commands;

import org.prevayler.demos.demo2.Account;

public class HolderChange extends AccountCommand {

	private final String newHolder;

	public HolderChange(Account account, String newHolder) {
		super(account);
		this.newHolder = newHolder;
	}

	public void execute(Account account) throws Account.InvalidHolder {
		account.holder(newHolder);
	}
}