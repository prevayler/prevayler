package prevayler.demos.rollbackdemo.commands;

import java.io.Serializable;
import prevayler.MementoCollector;
import prevayler.demos.rollbackdemo.Account;
import prevayler.demos.rollbackdemo.Bank;

public class AccountCreation extends BankCommand {

  private final String holder;
  
  public AccountCreation(String holder) {
    this.holder = holder;
  }
  
  protected void findObjects(Bank bank) {
  }
  
  protected void checkPrecondition() {
  }
  
  protected void createMementosAfterBank(MementoCollector collector) {
  }
  
  protected Serializable execute(Bank bank) throws Account.InvalidHolder {
    return bank.createAccount(holder);
  }
}
