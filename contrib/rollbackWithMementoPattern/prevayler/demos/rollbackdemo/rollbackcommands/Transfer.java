package prevayler.demos.rollbackdemo.rollbackcommands;

import java.io.Serializable;
import prevayler.PrevalentSystem;
import prevayler.MementoCollector;
import prevayler.MementoCommand;
import prevayler.demos.rollbackdemo.Bank;

public class Transfer extends MementoCommand {
  private long sourceNumber;
  private long destinationNumber;
  private long amount;

  /**
   * Set by findObjects(...)
   */  
  private transient Bank bank;  
  
  public Transfer(long sourceNumber, long destinationNumber, long amount) {
    super();
    
    this.sourceNumber = sourceNumber;
    this.destinationNumber = destinationNumber;
    this.amount = amount;
  }
  
  protected void findObjects(PrevalentSystem system) throws Exception {
    bank = (Bank)system;
  }

  protected void checkPrecondition() {
  }

  protected void createMementos(MementoCollector collector) {
  }

  protected Serializable execute(MementoCollector collector) throws Exception {
    MementoCommand command = new Withdrawal(sourceNumber, amount);
    command.execute(collector, bank);
    
    command = new Deposit(destinationNumber, amount);
    command.execute(collector, bank);
    
    return null;
  }

}
