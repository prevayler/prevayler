package prevayler.demos.rollbackdemo.commands;

import prevayler.MementoCollector;
import prevayler.demos.rollbackdemo.Bank;

/**
 * This class should no longer be used. It does not create the necessary mementos for error recovery.
 */
public class Transfer extends BankCommand {

  private final long originAccountNumber;
  private final long destinationAccountNumber;
  private final long amount;

  public Transfer(long originAccountNumber, long destinationAccountNumber, long amount) {
    this.originAccountNumber = originAccountNumber;
    this.destinationAccountNumber = destinationAccountNumber;
    this.amount = amount;
  }
  
  protected void findObjects(Bank bank) {
  }

  protected void checkPrecondition() {
  }

  protected void createMementosAfterBank(MementoCollector collector) {
  }
  
  public java.io.Serializable execute(Bank bank) throws Exception {
    bank.transfer(originAccountNumber, destinationAccountNumber, amount);
    return null;
  }
}
