package prevayler.demo;
	
import java.util.*;
import prevayler.*;

public class Bank implements PrevalentSystem {

	private long nextAccountNumber = 1;
	private final Map accountsByNumber = new HashMap();
	private final AlarmClock clock = new AlarmClock();
	private transient BankListener bankListener;
    
	public AlarmClock clock() {
		return clock;
	}
 
	public Account createAccount(String holder) throws Account.InvalidHolder {
		Account account = new Account(nextAccountNumber, holder, clock);
		accountsByNumber.put(new Long(nextAccountNumber++), account);
		
		if (bankListener != null) bankListener.accountCreated(account);
		return account;
	}

	public void deleteAccount(long number) throws AccountNotFound {
		Account account = findAccount(number);
		accountsByNumber.remove(new Long(number));
		if (bankListener != null) bankListener.accountDeleted(account);
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
	
	public void setBankListener(BankListener bankListener) {
		this.bankListener = bankListener;
	}
	
	public Account findAccount(long number) throws AccountNotFound {
		Account account = searchAccount(number);
		if (account == null) throw new AccountNotFound(number);
		return account;
	}

	public void transfer(long sourceNumber, long destinationNumber, long ammount) throws AccountNotFound, Account.InvalidAmmount {
		Account source = findAccount(sourceNumber);
		Account destination = findAccount(destinationNumber);

		source.withdraw(ammount);
		destination.deposit(ammount);
	}

	private Account searchAccount(long number) {
		return (Account)accountsByNumber.get(new Long(number));
	}

	public class AccountNotFound extends Exception {
		AccountNotFound(long number) {
			super("Account not found: " + Account.numberString(number) + ".\nMight have been deleted.");
		}
	}

}