package org.prevayler.demo.commands;

import org.prevayler.demo.Account;

public class Deposit extends AccountCommand {

	private final long amount;

	public Deposit(Account account, long amount) {
		super(account);
		this.amount = amount;
	}

	public void execute(Account account) throws Account.InvalidAmount {
		account.deposit(amount);
	}
}