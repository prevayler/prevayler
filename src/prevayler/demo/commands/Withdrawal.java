package prevayler.demo.commands;

import prevayler.demo.*;

public class Withdrawal extends AccountCommand {

	protected final long ammount;

	public Withdrawal(Account account, long ammount) {
		super(account);
		this.ammount = ammount;
	}

    public void execute(Account account) throws Account.InvalidAmmount {
		account.withdraw(ammount);
	}
}
