package org.prevayler.implementation.logging;

import java.io.Serializable;
import java.util.Date;

import org.prevayler.Transaction;

class TransactionLogEntry implements Serializable {

	final Transaction transaction;
	final Date timestamp;

	TransactionLogEntry(Transaction transaction, Date timestamp) {
		this.transaction = transaction;
		this.timestamp = timestamp;
	}


}
