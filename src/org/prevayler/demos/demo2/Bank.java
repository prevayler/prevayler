package org.prevayler.demos.demo2;
	
import java.util.*;
import org.prevayler.*;
import org.prevayler.implementation.AbstractPrevalentSystem;

public class Bank extends AbstractPrevalentSystem {

	private long nextAccountNumber = 1;
	private final Map accountsByNumber = new HashMap();
	private transient BankListener bankListener;
    
	public Account createAccount(String holder) throws Account.InvalidHolder {
		Account account = new Account(nextAccountNumber, holder, clock());
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

	public void transfer(long sourceNumber, long destinationNumber, long amount) throws AccountNotFound, Account.InvalidAmount {
		Account source = findAccount(sourceNumber);
		Account destination = findAccount(destinationNumber);

		source.withdraw(amount);
		destination.deposit(amount);
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