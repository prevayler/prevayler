package org.prevayler.demos.memento;
	
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import org.prevayler.util.memento.Memento;
import org.prevayler.util.memento.MementoCollector;

/**
 * The Bank class without the notification to a listener and including a memento.
 */
public class Bank implements java.io.Serializable {

	private long nextAccountNumber = 1;
	private Map accountsByNumber = new HashMap();

  /**
   * The memento of bank. Only (persistent) changeable fields/containers need to be stored.
   * In this case this means all fields.
   */
  private class BankMemento extends Memento {
    private long nextAccountNumber;
    private Map accountsByNumber;
    
    private BankMemento()
    {
      super();
      
      nextAccountNumber = Bank.this.nextAccountNumber;
      accountsByNumber = new HashMap(Bank.this.accountsByNumber);
    }
    
    protected void restore() {
      Bank.this.nextAccountNumber = nextAccountNumber;
      Bank.this.accountsByNumber.clear();
      Bank.this.accountsByNumber.putAll(accountsByNumber);
    }
    
    protected Object getOwner()
    {
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
		accountsByNumber.put(new Long(nextAccountNumber++), account);
		
		return account;
	}

	public void deleteAccount(long number) throws AccountNotFound {
		Account account = findAccount(number);
		accountsByNumber.remove(new Long(number));
	}
    
	public List accounts() {
		List accounts = new ArrayList(accountsByNumber.values());

		Collections.sort(accounts, new Comparator() {
			public int compare(Object acc1, Object acc2) {
				return ((Account)acc1).number() < ((Account)acc2).number() ? -1	: 1;
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
		return (Account)accountsByNumber.get(new Long(number));
	}

	public class AccountNotFound extends Exception {
		AccountNotFound(long number) {
			super("Account not found: " + Account.numberString(number) + ".\nMight have been deleted.");
		}
	}
    
  public String toString() {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    Iterator iterator;
    
    iterator = accountsByNumber.values().iterator();
    while (iterator.hasNext()) {
      writer.println(iterator.next());
    }
    
    writer.flush();
    
    return stringWriter.toString();
  }

}