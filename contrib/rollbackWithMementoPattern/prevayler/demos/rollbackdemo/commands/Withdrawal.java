package prevayler.demos.rollbackdemo.commands;

import prevayler.demos.rollbackdemo.Account;

public class Withdrawal extends AccountCommand {

	protected final long amount;

	public Withdrawal(Account account, long amount) {
		super(account);
		this.amount = amount;
	}

  protected void checkPrecondition() {
  }

  public void execute(Account account) throws Account.InvalidAmount {
		account.withdraw(amount);
	}
}
