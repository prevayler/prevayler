package prevayler.demos.rollbackdemo.commands;

import java.io.Serializable;
import prevayler.MementoCollector;
import prevayler.demos.rollbackdemo.Account;
import prevayler.demos.rollbackdemo.Bank;

/**
 * @author Johan Stuyts
 */
abstract class AccountCommand extends BankCommand {

	private final long accountNumber;

  /**
   * Set by findObjects(...)
   */  
  private transient Account account;

	protected AccountCommand(Account account) {
		accountNumber = account.number();
	}
    
  protected void findObjects(Bank bank) throws Exception{
    account = bank.findAccount(accountNumber);
  }
  
  protected final void createMementosAfterBank(MementoCollector collector) {
    account.createMemento(collector);
  }
  
	protected Serializable execute(Bank bank) throws Exception {
		execute(account);
		return null;
	}
	
	protected abstract void execute(Account account) throws Exception;
}