package org.prevayler.demos.demo2.commands;

import org.prevayler.demos.demo2.*;
import java.io.Serializable;

public class AccountDeletion extends BankCommand {

	private final long accountNumber;

	public AccountDeletion(Account account) {
		accountNumber = account.number();
	}

	protected Serializable execute(Bank bank) throws Bank.AccountNotFound {
		bank.deleteAccount(accountNumber);
		return null;
	}
}