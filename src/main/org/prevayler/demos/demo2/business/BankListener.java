package org.prevayler.demos.demo2.business;
	
public interface BankListener {
		
	public void accountCreated(Account account);
	
	public void accountDeleted(Account account);

}