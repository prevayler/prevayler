package prevayler.demos.rollbackdemo;
	
public interface BankListener {
		
	public void accountCreated(Account account);
	public void accountDeleted(Account account);

}