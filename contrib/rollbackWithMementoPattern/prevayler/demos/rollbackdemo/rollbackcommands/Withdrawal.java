package prevayler.demos.rollbackdemo.rollbackcommands;

import java.io.Serializable;
import javax.swing.JOptionPane;
import prevayler.PrevalentSystem;
import prevayler.MementoCollector;
import prevayler.MementoCommand;
import prevayler.demos.rollbackdemo.Account;
import prevayler.demos.rollbackdemo.Bank;

public class Withdrawal extends MementoCommand {
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
  
  protected void findObjects(PrevalentSystem system) throws Exception
  {
    if (JOptionPane.showConfirmDialog(null, "Fail at Withdrawal::findObjects?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }
    
    account = ((Bank)system).findAccount(accountNumber);
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
    
    account.withdraw(amount);
    
    return null;
  }

}
