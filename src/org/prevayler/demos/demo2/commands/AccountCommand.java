package org.prevayler.demos.demo2.commands;

import org.prevayler.demos.demo2.*;
import java.io.Serializable;

abstract class AccountCommand extends BankCommand {

	private final long accountNumber;

	protected AccountCommand(Account account) {
		accountNumber = account.number();
	}
    
	protected Serializable execute(Bank bank) throws Exception {
		execute(bank.findAccount(accountNumber));
		return null;
	}
	
	protected abstract void execute(Account account) throws Exception;
}