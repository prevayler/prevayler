package prevayler.demos.rollbackdemo.commands;

import prevayler.demos.rollbackdemo.Account;

public class HolderChange extends AccountCommand {

	private final String newHolder;

	public HolderChange(Account account, String newHolder) {
		super(account);
		this.newHolder = newHolder;
	}

  protected void checkPrecondition() {
  }

	public void execute(Account account) throws Account.InvalidHolder {
		account.holder(newHolder);
	}
}