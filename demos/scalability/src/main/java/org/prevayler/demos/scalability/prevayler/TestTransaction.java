package org.prevayler.demos.scalability.prevayler;

import org.prevayler.Transaction;
import org.prevayler.demos.scalability.Record;

import java.util.Date;

class TestTransaction implements Transaction<TransactionSystem> {

  private static final long serialVersionUID = -2634307328586761351L;
  private final Record recordToInsert;
  private final Record recordToUpdate;
  private final long idToDelete;

  TestTransaction(Record recordToInsert, Record recordToUpdate, long idToDelete) {
    this.recordToInsert = recordToInsert;
    this.recordToUpdate = recordToUpdate;
    this.idToDelete = idToDelete;
  }

  public void executeOn(TransactionSystem system, Date ignored) {
    system.performTransaction(recordToInsert, recordToUpdate, idToDelete);
  }
}
