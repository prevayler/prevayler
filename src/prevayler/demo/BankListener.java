package prevayler.demo;
	
public interface BankListener {
		
	public void accountCreated(Account account);
	public void accountDeleted(Account account);

}