package prevayler.demo;

import prevayler.AlarmClock;
import java.util.*;

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
    
	public long number() {
		return number;
	}

	public String toString() { //Returns something like "00123 - John Smith"
		return numberString() + " - " + holder;
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

	public void deposit(long ammount) throws InvalidAmmount {
		verify(ammount);
        register(ammount);
	}

	public void withdraw(long ammount) throws InvalidAmmount {
		verify(ammount);
        register(-ammount);
	}

    private void register(long ammount) {
		balance += ammount;
        transactionHistory.add(new Transaction(ammount));
		notifyListeners();
	}
    
	private void verify(long ammount) throws InvalidAmmount {
		if (ammount <= 0) throw new InvalidAmmount("Ammount must be greater than zero.");
		if (ammount > 10000) throw new InvalidAmmount("Ammount maximum (10000) exceeded.");
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

	public class InvalidAmmount extends Exception {
		InvalidAmmount(String message) {
			super(message);
		}
	}

	private void verify(String holder) throws InvalidHolder {
		if (holder == null || holder.equals("")) throw new InvalidHolder();
	}

	public class InvalidHolder extends Exception {
		InvalidHolder() {
			super("Invalid holder name.");
		}
	}

    private class Transaction implements java.io.Serializable {

        private final long ammount;
        private final Date timestamp;

        private Transaction(long ammount) {
            this.ammount = ammount;
            this.timestamp = clock.time();
        }

        public String toString() {
            return timestampString() + "      Ammount: " + ammount;
        }

        private String timestampString() {
            return new java.text.SimpleDateFormat("yyyy/MM/dd  hh:mm:ss.SSS").format(timestamp);
        }


    }
    
}
