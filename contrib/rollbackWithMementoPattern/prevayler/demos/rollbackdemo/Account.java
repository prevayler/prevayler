package prevayler.demos.rollbackdemo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import prevayler.AlarmClock;
import prevayler.Memento;
import prevayler.MementoCollector;

/**
 * @author Johan Stuyts
 */
public class Account implements java.io.Serializable {

	private final long number;
	private final AlarmClock clock;
	private String holder;
	private long balance = 0;
	private List transactionHistory = new ArrayList();
	private transient Set listeners;
    
	Account(long number, String holder, AlarmClock clock) throws InvalidHolder {
		this.clock = clock;
		this.number = number;
		holder(holder);
	}
  
  /**
   * The memento of account. Only (persistent) changeable fields/containers need to be stored. In this case this means the holder, the balance and the transaction history.
   */
  private class AccountMemento implements Memento {
    private String holder;
    private long balance;
    private List transactionHistory;
    
    private AccountMemento() {
      super();
      
      holder = Account.this.holder;
      balance = Account.this.balance;
      transactionHistory = new ArrayList(Account.this.transactionHistory);
    }
    
    public void restore() {
      Account.this.holder = holder;
      Account.this.balance = balance;
      Account.this.transactionHistory.clear();
      Account.this.transactionHistory.addAll(transactionHistory);
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
		return numberString() + " - " + holder  + " - " + balance;
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
		notifyListeners();
	}
    
	public long balance() {
		return balance;
	}

	public void deposit(long amount) throws InvalidAmount {
		verify(amount);
        register(amount);
	}

	public void withdraw(long amount) throws InvalidAmount {
		verify(amount);
        register(-amount);
	}

    private void register(long amount) {
		balance += amount;
        transactionHistory.add(new Transaction(amount));
		notifyListeners();
	}
    
	private void verify(long amount) throws InvalidAmount {
		if (amount <= 0) throw new InvalidAmount("Amount must be greater than zero.");
		if (amount > 10000) throw new InvalidAmount("Amount maximum (10000) exceeded.");
	}

    public List transactionHistory() {
        return transactionHistory;
    }

	public void addAccountListener(AccountListener listener) {
		listeners().add(listener);
	}

	public void removeAccountListener(AccountListener listener) {
		listeners().remove(listener);
	}
	
	private Set listeners() {
		if (listeners == null) listeners = new HashSet();
		return listeners;
	}
	
	private void notifyListeners() {
		Iterator it = listeners().iterator();
		while (it.hasNext()) {
			((AccountListener)it.next()).accountChanged();
		}
	}

	public class InvalidAmount extends Exception {
		public InvalidAmount(String message) {
			super(message);
		}
	}

	private void verify(String holder) throws InvalidHolder {
		if (holder == null || holder.equals("")) throw new InvalidHolder();
	}

	public class InvalidHolder extends Exception {
		public InvalidHolder() {
			super("Invalid holder name.");
		}
	}

    private class Transaction implements java.io.Serializable {

        private final long amount;
        private final Date timestamp;

        private Transaction(long amount) {
            this.amount = amount;
            this.timestamp = clock.time();
        }

        public String toString() {
            return timestampString() + "      Amount: " + amount;
        }

        private String timestampString() {
            return new java.text.SimpleDateFormat("yyyy/MM/dd  hh:mm:ss.SSS").format(timestamp);
        }


    }
    
}
