package org.prevayler.demos.demo2.commands;

import org.prevayler.demos.demo2.*;

public class Withdrawal extends AccountCommand {

	protected final long amount;

	public Withdrawal(Account account, long amount) {
		super(account);
		this.amount = amount;
	}

    public void execute(Account account) throws Account.InvalidAmount {
		account.withdraw(amount);
	}
}
