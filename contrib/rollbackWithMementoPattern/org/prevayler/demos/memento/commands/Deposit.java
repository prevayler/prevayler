package org.prevayler.demos.memento.commands;

import java.io.Serializable;
import javax.swing.JOptionPane;
import java.util.Date;
import org.prevayler.util.memento.MementoCollector;
import org.prevayler.util.memento.MementoTransaction;
import org.prevayler.demos.memento.Account;
import org.prevayler.demos.memento.Bank;

public class Deposit extends MementoTransaction {
  private long accountNumber;
  private long amount;
  
  /**
   * Set by findObjects(...)
   */  
  private transient Account account;
  
  public Deposit(Account account, long amount) {
    this(account.number(), amount);
  }
  
  public Deposit(long accountNumber, long amount) {
    super();
    
    this.accountNumber = accountNumber;
    this.amount = amount;
  }
  
  protected void findObjects(Object prevalentSystem) throws Exception
  {
    if (JOptionPane.showConfirmDialog(null, "Fail at Deposit::findObjects?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }
    
    account = ((Bank)prevalentSystem).findAccount(accountNumber);
  }

  protected void checkPrecondition()
  {
    if (JOptionPane.showConfirmDialog(null, "Fail at Deposit::checkPrecondition?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }
    
  }

  protected void createMementos(MementoCollector collector)
  {
    if (JOptionPane.showConfirmDialog(null, "Fail at Deposit::createMemento?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }
    
    account.createMemento(collector);
  }

  protected Serializable execute(MementoCollector collector) throws Exception
  {
    if (JOptionPane.showConfirmDialog(null, "Fail at Deposit::execute?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }
    
    account.deposit(amount, new Date()); //dummy'ed date to make things work
    
    System.out.println("* Deposited " + amount + " into account " + account.numberString());
    
    return null;
  }

}
