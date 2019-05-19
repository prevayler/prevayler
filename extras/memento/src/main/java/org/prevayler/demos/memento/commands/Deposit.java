package org.prevayler.demos.memento.commands;

import org.prevayler.demos.memento.Account;
import org.prevayler.demos.memento.Bank;
import org.prevayler.util.memento.MementoCollector;
import org.prevayler.util.memento.MementoTransaction;

import javax.swing.*;
import java.util.Date;

public class Deposit extends MementoTransaction {
  private static final long serialVersionUID = -154783426607714557L;
  private long accountNumber;
  private long amount;

  /**
   * Set by findObjects(...)
   */
  private transient Account account;

  public Deposit(Account account, long amount) {
    this(account.number(), amount);
  }

  public Deposit(long accountNumber, long amount) {
    super();

    this.accountNumber = accountNumber;
    this.amount = amount;
  }

  protected void findObjects(Bank prevalentSystem) throws Exception {
    if (JOptionPane.showConfirmDialog(null, "Fail at Deposit::findObjects?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }

    account = prevalentSystem.findAccount(accountNumber);
  }

  protected void checkPrecondition() {
    if (JOptionPane.showConfirmDialog(null, "Fail at Deposit::checkPrecondition?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }

  }

  protected void createMementos(MementoCollector collector) {
    if (JOptionPane.showConfirmDialog(null, "Fail at Deposit::createMemento?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }

    account.createMemento(collector);
  }

  protected Account execute(MementoCollector collector) throws Exception {
    if (JOptionPane.showConfirmDialog(null, "Fail at Deposit::execute?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }

    account.deposit(amount, new Date()); //dummy'ed date to make things work

    System.out.println("* Deposited " + amount + " into account " + account.numberString());

    return null;
  }

}
