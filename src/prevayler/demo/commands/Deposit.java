package prevayler.demo.commands;

import prevayler.demo.Account;

public class Deposit extends AccountCommand {

	private final long ammount;

	public Deposit(Account account, long ammount) {
		super(account);
		this.ammount = ammount;
	}

	public void execute(Account account) throws Account.InvalidAmmount {
		account.deposit(ammount);
	}
}