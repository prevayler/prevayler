//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import org.prevayler.Transaction;
import org.prevayler.foundation.DeepCopier;
import org.prevayler.foundation.serialization.Serializer;

import java.io.Serializable;
import java.util.Date;

public class TransactionTimestamp implements Serializable {

	static final long serialVersionUID = 1L;

	private final Transaction _transaction;
	private final long _systemVersion;
	private final long _executionTime;

	public TransactionTimestamp(Transaction transaction, long systemVersion, Date executionTime) {
		this(transaction, systemVersion, executionTime.getTime());
	}

	private TransactionTimestamp(Transaction transaction, long systemVersion, long executionTime) {
		_transaction = transaction;
		_systemVersion = systemVersion;
		_executionTime = executionTime;
	}

	public Transaction transaction() {
		return _transaction;
	}

	public long systemVersion() {
		return _systemVersion;
	}

	public Date executionTime() {
		return new Date(_executionTime);
	}

	public TransactionTimestamp deepCopy(Serializer journalSerializer) {
		try {
			Transaction transactionCopy = (Transaction) DeepCopier.deepCopy(_transaction, journalSerializer);
			return new TransactionTimestamp(transactionCopy, _systemVersion, _executionTime);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Unable to produce a copy of the transaction for trying out before applying it to the real system.");
		}
	}

}
