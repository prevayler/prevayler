//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import java.io.Serializable;
import java.util.Date;

import org.prevayler.Transaction;

public class TransactionTimestamp implements Serializable {

    static final long serialVersionUID = 0L;

	private final Transaction transaction;
	private final long timestamp;

	public TransactionTimestamp(Transaction transaction, Date timestamp) {
		this.transaction = transaction;
		this.timestamp = timestamp.getTime();
	}

    public Transaction transaction() {
        return this.transaction;
    }

    public Date timestamp() {
        return new Date(this.timestamp);
    }

}
