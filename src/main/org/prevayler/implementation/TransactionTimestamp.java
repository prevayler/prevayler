//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import org.prevayler.Transaction;

import java.io.Serializable;
import java.util.Date;

public class TransactionTimestamp implements Serializable {

    static final long serialVersionUID = 1L;

	private final Transaction _transaction;
	private final long _systemVersion;
	private final long _executionTime;

	public TransactionTimestamp(Transaction transaction, long systemVersion, Date executionTime) {
		_transaction = transaction;
		_systemVersion = systemVersion;
		_executionTime = executionTime.getTime();
	}

    public Transaction transaction() {
        return _transaction;
    }

	public long systemVersion() {
		return _systemVersion;
	}

    public Date timestamp() {
        return new Date(_executionTime);
    }

}
