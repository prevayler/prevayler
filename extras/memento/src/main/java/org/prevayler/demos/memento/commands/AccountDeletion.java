package org.prevayler.demos.memento.commands;

import java.io.Serializable;
import org.prevayler.util.memento.MementoCollector;
import org.prevayler.util.memento.MementoTransaction;
import org.prevayler.demos.memento.Account;
import org.prevayler.demos.memento.Bank;

public class AccountDeletion extends MementoTransaction {
	private final long accountNumber;

  /**
   * Set by findObjects(...)
   */  
  protected transient Bank bank;
  
	public AccountDeletion(Account account) {
		accountNumber = account.number();
	}
  
  protected void findObjects(Object prevalentSystem) {
    bank = (Bank)prevalentSystem;
  }
  
  protected void checkPrecondition() {
  }

  protected void createMementos(MementoCollector collector) {
    bank.createMemento(collector);
  }
  
	protected Serializable execute(MementoCollector collector) throws Bank.AccountNotFound {
		bank.deleteAccount(accountNumber);
		return null;
	}
}