package org.prevayler.demos.memento;

import org.prevayler.util.memento.Memento;
import org.prevayler.util.memento.MementoCollector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Account class without the notification to listeners and including a memento.
 */
public class Account implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  private long number;
  private String holder;
  private long balance = 0;
  private List<Transaction> transactionHistory = new ArrayList<Transaction>();

  @SuppressWarnings("unused")
  private Account() {
  } //Is this used by some serializer that requires no-arg constructors?

  Account(long number, String holder) throws InvalidHolder {
    this.number = number;
    holder(holder);
  }

  /**
   * The memento of account. Only (persistent) changeable fields/containers need to be stored. In this case this means the holder, the balance and the transaction history.
   */
  private class AccountMemento extends Memento {
    private String holder;
    private long balance;
    private List<Transaction> transactionHistory;

    private AccountMemento() {
      super();

      holder = Account.this.holder;
      balance = Account.this.balance;
      transactionHistory = new ArrayList<Transaction>(Account.this.transactionHistory);
    }

    protected void restore() {
      Account.this.holder = holder;
      Account.this.balance = balance;
      Account.this.transactionHistory.clear();
      Account.this.transactionHistory.addAll(transactionHistory);
    }

    protected Object getOwner() {
      return Account.this;
    }
  }

  /**
   * Create a memento of the current state.
   */
  public void createMemento(MementoCollector collector) {
    collector.addMemento(new AccountMemento());
  }

  public long number() {
    return number;
  }

  public String toString() { //Returns something like "00123 - John Smith"
    return numberString() + " - " + holder + " - " + balance;
  }

  public String numberString() {
    return numberString(number);
  }

  static String numberString(long number) {
    return (new java.text.DecimalFormat("00000").format(number));
  }

  public String holder() {
    return holder;
  }

  public void holder(String holder) throws InvalidHolder {
    verify(holder);
    this.holder = holder;
  }

  public long balance() {
    return balance;
  }

  public void deposit(long amount, Date timestamp) throws InvalidAmount {
    verify(amount);
    register(amount, timestamp);
  }

  public void withdraw(long amount, Date timestamp) throws InvalidAmount {
    verify(amount);
    register(-amount, timestamp);
  }

  private void register(long amount, Date timestamp) {
    balance += amount;
    transactionHistory.add(new Transaction(amount, timestamp));
  }

  private void verify(long amount) throws InvalidAmount {
    if (amount <= 0) throw new InvalidAmount("Amount must be greater than zero.");
    if (amount > 10000) throw new InvalidAmount("Amount maximum (10000) exceeded.");
  }

  public List<Transaction> transactionHistory() {
    return transactionHistory;
  }

  public class InvalidAmount extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidAmount(String message) {
      super(message);
    }
  }

  private void verify(String holder) throws InvalidHolder {
    if (holder == null || holder.equals("")) throw new InvalidHolder();
  }

  public class InvalidHolder extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidHolder() {
      super("Invalid holder name.");
    }
  }

  private class Transaction implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final long amount;
    private final Date timestamp;

    private Transaction(long amount, Date timestamp) {
      this.amount = amount;
      this.timestamp = timestamp;
    }

    public String toString() {
      return timestampString() + "      Amount: " + amount;
    }

    private String timestampString() {
      return new java.text.SimpleDateFormat("yyyy/MM/dd  hh:mm:ss.SSS").format(timestamp);
    }


  }

}
