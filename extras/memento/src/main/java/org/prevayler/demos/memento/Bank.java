package org.prevayler.demos.memento;

import org.prevayler.util.memento.Memento;
import org.prevayler.util.memento.MementoCollector;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * The Bank class without the notification to a listener and including a memento.
 */
public class Bank implements java.io.Serializable {

  private static final long serialVersionUID = 6025629369302269753L;
  private long nextAccountNumber = 1;
  private Map<Long, Account> accountsByNumber = new HashMap<Long, Account>();

  /**
   * The memento of bank. Only (persistent) changeable fields/containers need to be stored.
   * In this case this means all fields.
   */
  private class BankMemento extends Memento {
    private long nextAccountNumber;
    private Map<Long, Account> accountsByNumber;

    private BankMemento() {
      super();

      nextAccountNumber = Bank.this.nextAccountNumber;
      accountsByNumber = new HashMap<Long, Account>(Bank.this.accountsByNumber);
    }

    protected void restore() {
      Bank.this.nextAccountNumber = nextAccountNumber;
      Bank.this.accountsByNumber.clear();
      Bank.this.accountsByNumber.putAll(accountsByNumber);
    }

    protected Object getOwner() {
      return Bank.this;
    }
  }

  /**
   * Create a memento of the current state.
   */
  public void createMemento(MementoCollector collector) {
    collector.addMemento(new BankMemento());
  }

  public Account createAccount(String holder) throws Account.InvalidHolder {
    Account account = new Account(nextAccountNumber, holder);
    accountsByNumber.put(nextAccountNumber++, account);

    return account;
  }

  public void deleteAccount(long number) throws AccountNotFound {
    findAccount(number);
    accountsByNumber.remove(Long.valueOf(number));
  }

  public List<Account> accounts() {
    List<Account> accounts = new ArrayList<Account>(accountsByNumber.values());

    Collections.sort(accounts, new Comparator<Account>() {
      public int compare(Account acc1, Account acc2) {
        return acc1.number() < acc2.number() ? -1 : 1;
      }
    });

    return accounts;
  }

  public Account findAccount(long number) throws AccountNotFound {
    Account account = searchAccount(number);
    if (account == null) throw new AccountNotFound(number);
    return account;
  }

  public void transfer(long sourceNumber, long destinationNumber, long amount, Date timestamp) throws AccountNotFound, Account.InvalidAmount {
    Account source = findAccount(sourceNumber);
    Account destination = findAccount(destinationNumber);

    source.withdraw(amount, timestamp);
    destination.deposit(amount, timestamp);
  }

  private Account searchAccount(long number) {
    return accountsByNumber.get(Long.valueOf(number));
  }

  public class AccountNotFound extends Exception {
    private static final long serialVersionUID = 4463910784646858052L;

    AccountNotFound(long number) {
      super("Account not found: " + Account.numberString(number) + ".\nMight have been deleted.");
    }
  }

  public String toString() {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    Iterator<Account> iterator;

    iterator = accountsByNumber.values().iterator();
    while (iterator.hasNext()) {
      writer.println(iterator.next());
    }

    writer.flush();

    return stringWriter.toString();
  }

}
