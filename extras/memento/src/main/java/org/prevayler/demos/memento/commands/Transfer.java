package org.prevayler.demos.memento.commands;

import java.io.Serializable;
import org.prevayler.util.memento.MementoCollector;
import org.prevayler.util.memento.MementoTransaction;
import org.prevayler.demos.memento.Bank;

public class Transfer extends MementoTransaction {
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
  
  protected void findObjects(Object prevalentSystem) throws Exception {
    bank = (Bank)prevalentSystem;
  }

  protected void checkPrecondition() {
  }

  protected void createMementos(MementoCollector collector) {
  }

  protected Serializable execute(MementoCollector collector) throws Exception {
    MementoTransaction command = new Withdrawal(sourceNumber, amount);
    command.execute(collector, bank);
    
    command = new Deposit(destinationNumber, amount);
    command.execute(collector, bank);
    
    return null;
  }

}
