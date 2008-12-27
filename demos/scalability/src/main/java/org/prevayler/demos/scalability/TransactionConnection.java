package org.prevayler.demos.scalability;

public interface TransactionConnection {

	public void performTransaction(Record recordToInsert, Record recordToUpdate, long idToDelete);

}
