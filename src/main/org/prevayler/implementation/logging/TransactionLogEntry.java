package org.prevayler.implementation.logging;

import java.io.Serializable;
import java.util.Date;

import org.prevayler.Transaction;

class TransactionLogEntry implements Serializable { //TODO Eliminate duplicity with TransactionTimestamp.

	final Transaction transaction;
	final Date timestamp;

	TransactionLogEntry(Transaction transaction, Date timestamp) {
		this.transaction = transaction;
		this.timestamp = timestamp;
	}


}
