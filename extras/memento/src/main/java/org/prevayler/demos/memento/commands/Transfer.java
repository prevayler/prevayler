package org.prevayler.demos.memento.commands;

import org.prevayler.demos.memento.Account;
import org.prevayler.demos.memento.Bank;
import org.prevayler.util.memento.MementoCollector;
import org.prevayler.util.memento.MementoTransaction;

public class Transfer extends MementoTransaction {
  private static final long serialVersionUID = -6700799319722269361L;
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

  protected void findObjects(Bank prevalentSystem) throws Exception {
    bank = prevalentSystem;
  }

  protected void checkPrecondition() {
  }

  protected void createMementos(MementoCollector collector) {
  }

  protected Account execute(MementoCollector collector) throws Exception {
    MementoTransaction command = new Withdrawal(sourceNumber, amount);
    command.execute(collector, bank);

    command = new Deposit(destinationNumber, amount);
    command.execute(collector, bank);

    return null;
  }

}
