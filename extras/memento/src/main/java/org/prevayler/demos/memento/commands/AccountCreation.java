package org.prevayler.demos.memento.commands;

import org.prevayler.demos.memento.Account;
import org.prevayler.demos.memento.Bank;
import org.prevayler.util.memento.MementoCollector;
import org.prevayler.util.memento.MementoTransaction;

public class AccountCreation extends MementoTransaction {
  private static final long serialVersionUID = 591298522015413614L;

  private final String holder;

  /**
   * Set by findObjects(...)
   */
  protected transient Bank bank;

  public AccountCreation(String holder) {
    this.holder = holder;
  }

  protected void findObjects(Bank prevalentSystem) {
    bank = prevalentSystem;
  }

  protected void checkPrecondition() {
  }

  protected void createMementos(MementoCollector collector) {
    bank.createMemento(collector);
  }

  protected Account execute(MementoCollector collector) throws Account.InvalidHolder {
    return bank.createAccount(holder);
  }
}
