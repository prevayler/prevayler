package org.prevayler.demos.scalability.prevayler;

import org.prevayler.demos.scalability.*;
import org.prevayler.Prevayler;

class PrevaylerTransactionConnection implements TransactionConnection {

	private final Prevayler	prevayler;

	PrevaylerTransactionConnection(Prevayler prevayler) {
		this.prevayler = prevayler;
	}

	public void performTransaction(Record recordToInsert, Record recordToUpdate, long idToDelete) {
		try {

			prevayler.execute(new TestTransaction(recordToInsert, recordToUpdate, idToDelete));

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Unexpected Exception: " + ex);
		}
	}
}
