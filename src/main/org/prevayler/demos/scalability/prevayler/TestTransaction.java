package org.prevayler.demos.scalability.prevayler;

import java.util.Date;

import org.prevayler.Transaction;
import org.prevayler.demos.scalability.Record;

class TestTransaction implements Transaction {

	private final Record recordToInsert;
	private final Record recordToUpdate;
	private final long idToDelete;

	TestTransaction(Record recordToInsert, Record recordToUpdate, long idToDelete) {
		this.recordToInsert = recordToInsert;
		this.recordToUpdate = recordToUpdate;
		this.idToDelete = idToDelete;
	}

	public void executeOn(Object system, Date ignored) {
		((TransactionSystem)system).performTransaction(recordToInsert, recordToUpdate, idToDelete);
	}
}
