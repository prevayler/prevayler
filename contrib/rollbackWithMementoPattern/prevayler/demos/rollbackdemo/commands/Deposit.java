package prevayler.demos.rollbackdemo.commands;

import prevayler.demos.rollbackdemo.Account;

public class Deposit extends AccountCommand {

	private final long amount;

	public Deposit(Account account, long amount) {
		super(account);
		this.amount = amount;
	}

  protected void checkPrecondition() {
  }

	public void execute(Account account) throws Account.InvalidAmount {
		account.deposit(amount);
	}
}