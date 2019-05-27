package org.prevayler.demos.memento.commands;

import org.prevayler.demos.memento.Account;
import org.prevayler.demos.memento.Bank;
import org.prevayler.util.memento.MementoCollector;
import org.prevayler.util.memento.MementoTransaction;

public class AccountDeletion extends MementoTransaction {
  private static final long serialVersionUID = -3883591469544966498L;

  private final long accountNumber;

  /**
   * Set by findObjects(...)
   */
  protected transient Bank bank;

  public AccountDeletion(Account account) {
    accountNumber = account.number();
  }

  protected void findObjects(Bank prevalentSystem) {
    bank = prevalentSystem;
  }

  protected void checkPrecondition() {
  }

  protected void createMementos(MementoCollector collector) {
    bank.createMemento(collector);
  }

  protected Account execute(MementoCollector collector) throws Bank.AccountNotFound {
    bank.deleteAccount(accountNumber);
    return null;
  }
}