package org.prevayler.demos.memento.commands;

import java.io.Serializable;
import javax.swing.JOptionPane;
import java.util.Date;
import org.prevayler.util.memento.MementoCollector;
import org.prevayler.util.memento.MementoTransaction;
import org.prevayler.demos.memento.Account;
import org.prevayler.demos.memento.Bank;

public class Withdrawal extends MementoTransaction {
  private long accountNumber;
  private long amount;
  
  /**
   * Set by findObjects(...)
   */  
  private transient Account account;
  
  public Withdrawal(long accountNumber, long amount) {
    super();
    
    this.accountNumber = accountNumber;
    this.amount = amount;
  }
  
  protected void findObjects(Object prevalentSystem) throws Exception
  {
    if (JOptionPane.showConfirmDialog(null, "Fail at Withdrawal::findObjects?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }
    
    account = ((Bank)prevalentSystem).findAccount(accountNumber);
  }

  protected void checkPrecondition() throws Exception
  {
    if (JOptionPane.showConfirmDialog(null, "Fail at Withdrawal::checkPrecondition?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }
    
    if (account.balance() < amount) {
      throw account.new InvalidAmount("Can not withdraw more than the balance");
    }
  }

  protected void createMementos(MementoCollector collector)
  {
    if (JOptionPane.showConfirmDialog(null, "Fail at Withdrawal::createMemento?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }
    
    account.createMemento(collector);
  }

  protected Serializable execute(MementoCollector collector) throws Exception
  {
    if (JOptionPane.showConfirmDialog(null, "Fail at Withdrawal::execute?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }
    
    account.withdraw(amount, new Date()); //dummy'ed date to make things work
    
    System.out.println("* Withdrew " + amount + " from account " + account.numberString());
    
    return null;
  }

}
