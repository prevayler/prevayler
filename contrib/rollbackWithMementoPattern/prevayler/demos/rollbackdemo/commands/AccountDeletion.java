package prevayler.demos.rollbackdemo.commands;

import java.io.Serializable;
import prevayler.MementoCollector;
import prevayler.demos.rollbackdemo.Account;
import prevayler.demos.rollbackdemo.Bank;

public class AccountDeletion extends BankCommand {

	private final long accountNumber;

	public AccountDeletion(Account account) {
		accountNumber = account.number();
	}
  
  protected void findObjects(Bank bank) {
  }
  
  protected void checkPrecondition() {
  }

  protected void createMementosAfterBank(MementoCollector collector) {
  }
  
	protected Serializable execute(Bank bank) throws Bank.AccountNotFound {
		bank.deleteAccount(accountNumber);
		return null;
	}
}