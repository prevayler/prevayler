package org.prevayler.demos.memento.commands;

import java.io.Serializable;
import org.prevayler.util.memento.MementoCollector;
import org.prevayler.util.memento.MementoTransaction;
import org.prevayler.demos.memento.Account;
import org.prevayler.demos.memento.Bank;

public class AccountCreation extends MementoTransaction {
  private final String holder;
  
  /**
   * Set by findObjects(...)
   */  
  protected transient Bank bank;
  
  public AccountCreation(String holder) {
    this.holder = holder;
  }
  
  protected void findObjects(Object prevalentSystem) {
    bank = (Bank)prevalentSystem;
  }
  
  protected void checkPrecondition() {
  }
  
  protected void createMementos(MementoCollector collector) {
    bank.createMemento(collector);
  }
  
  protected Serializable execute(MementoCollector collector) throws Account.InvalidHolder {
    return bank.createAccount(holder);
  }
}
