package org.prevayler.demos.memento.commands;

import org.prevayler.demos.memento.Account;
import org.prevayler.demos.memento.Bank;
import org.prevayler.util.memento.MementoCollector;
import org.prevayler.util.memento.MementoTransaction;

import javax.swing.*;
import java.util.Date;

public class Withdrawal extends MementoTransaction {
  private static final long serialVersionUID = -1938471248356260702L;
  private long accountNumber;
  private long amount;

  /**
   * Set by findObjects(...)
   */
  private transient Account account;

  public Withdrawal(long accountNumber, long amount) {
    super();

    this.accountNumber = accountNumber;
    this.amount = amount;
  }

  protected void findObjects(Bank prevalentSystem) throws Exception {
    if (JOptionPane.showConfirmDialog(null, "Fail at Withdrawal::findObjects?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }

    account = prevalentSystem.findAccount(accountNumber);
  }

  protected void checkPrecondition() throws Exception {
    if (JOptionPane.showConfirmDialog(null, "Fail at Withdrawal::checkPrecondition?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }

    if (account.balance() < amount) {
      throw account.new InvalidAmount("Can not withdraw more than the balance");
    }
  }

  protected void createMementos(MementoCollector collector) {
    if (JOptionPane.showConfirmDialog(null, "Fail at Withdrawal::createMemento?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }

    account.createMemento(collector);
  }

  protected Account execute(MementoCollector collector) throws Exception {
    if (JOptionPane.showConfirmDialog(null, "Fail at Withdrawal::execute?", "Prevayler with rollback", JOptionPane.YES_NO_OPTION) == 0) {
      throw new RuntimeException();
    }

    account.withdraw(amount, new Date()); //dummy'ed date to make things work

    System.out.println("* Withdrew " + amount + " from account " + account.numberString());

    return null;
  }

}
