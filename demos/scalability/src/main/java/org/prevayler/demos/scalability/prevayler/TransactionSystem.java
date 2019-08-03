package org.prevayler.demos.scalability.prevayler;

import org.prevayler.demos.scalability.Record;
import org.prevayler.demos.scalability.RecordIterator;

import java.util.HashMap;
import java.util.Map;

class TransactionSystem implements ScalabilitySystem {

  private static final long serialVersionUID = 461535927650714306L;
  private final Map<Long, Record> recordsById = new HashMap<Long, Record>();

  public void performTransaction(Record recordToInsert, Record recordToUpdate, long idToDelete) {
    synchronized (recordsById) {
      put(recordToInsert);
      put(recordToUpdate);
      recordsById.remove(Long.valueOf(idToDelete));
    }
  }

  private Object put(Record newRecord) {
    return recordsById.put(newRecord.getId(), newRecord);
  }

  public void replaceAllRecords(RecordIterator newRecords) {
    recordsById.clear();

    while (newRecords.hasNext()) {
      put(newRecords.next());
    }
  }


  public int hashCode() {
    return recordsById.hashCode();
  }
}
