package org.prevayler.demo.commands;

import org.prevayler.demo.*;
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