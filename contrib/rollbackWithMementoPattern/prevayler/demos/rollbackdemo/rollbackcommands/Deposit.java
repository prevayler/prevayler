package prevayler.demos.rollbackdemo.rollbackcommands;

import java.io.Serializable;
import javax.swing.JOptionPane;
import prevayler.PrevalentSystem;
import prevayler.MementoCollector;
import prevayler.MementoCommand;
import prevayler.demos.rollbackdemo.Account;
import prevayler.demos.rollbackdemo.Bank;

public class Deposit extends MementoCommand {
  private long accountNumber;
  private long amount;
  
  /**
   * Set by findObjects(...)
   */  
  private transient Account account;
  
  public Deposit(long accountNumber, long amount) {
    super();
    
    this.accountNumber = accountNumber;
    this.amount = amount;
  }
  
  protected void findObjects(PrevalentSystem system) throws Exception
  {
    if (JOptionPane.showConfirmDialog(null, "Fail at Deposit::findObjects?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }
    
    account = ((Bank)system).findAccount(accountNumber);
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
    
    account.deposit(amount);
    
    return null;
  }

}
