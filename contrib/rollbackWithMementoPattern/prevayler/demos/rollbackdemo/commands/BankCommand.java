package prevayler.demos.rollbackdemo.commands;

import java.io.Serializable;
import prevayler.PrevalentSystem;
import prevayler.MementoCollector;
import prevayler.MementoCommand;
import prevayler.demos.rollbackdemo.Bank;

public abstract class BankCommand extends MementoCommand {
  /**
   * Set by findObjects(...)
   */  
  private transient Bank bank;

	protected Serializable execute(MementoCollector collector) throws Exception {
  	return execute(bank);
	}
    
	protected abstract Serializable execute(Bank bank) throws Exception;
  
  protected final void findObjects(PrevalentSystem system) throws Exception {
    bank = (Bank)system;
    findObjects(bank);
  }
  
  /**
   * Enable subclasses to find objects.
   */
  protected abstract void findObjects(Bank bank) throws Exception;
  
  protected final void createMementos(MementoCollector collector) {
    bank.createMemento(collector);
    createMementosAfterBank(collector);
  }
  
  /**
   * Enable subclasses to create mementos.
   */
  protected abstract void createMementosAfterBank(MementoCollector collector);
}