package org.prevayler.demos.demo2.business.transactions;

import org.prevayler.demos.demo2.business.Bank;

public class Transfer extends BankTransaction {

	private final long _originAccountNumber;
	private final long _destinationAccountNumber;
	private final long _amount;


	public Transfer(long originAccountNumber, long destinationAccountNumber, long amount) {
		_originAccountNumber = originAccountNumber;
		_destinationAccountNumber = destinationAccountNumber;
		_amount = amount;
	}


	public Object executeAndQuery(Bank bank) throws Exception {
		bank.transfer(_originAccountNumber, _destinationAccountNumber, _amount);
		return null;
	}

}
