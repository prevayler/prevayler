package org.prevayler.demos.demo2.business.transactions;

import java.util.Date;

import org.prevayler.demos.demo2.business.Bank;

public class Transfer extends BankTransaction {

	private static final long serialVersionUID = -8656365915179217238L;
	private long _originAccountNumber;
	private long _destinationAccountNumber;
	private long _amount;


    private Transfer() {} //Necessary for Skaringa XML serialization
	public Transfer(long originAccountNumber, long destinationAccountNumber, long amount) {
		_originAccountNumber = originAccountNumber;
		_destinationAccountNumber = destinationAccountNumber;
		_amount = amount;
	}


	public Object executeAndQuery(Bank bank, Date timestamp) throws Exception {
		bank.transfer(_originAccountNumber, _destinationAccountNumber, _amount, timestamp);
		return null;
	}

}
