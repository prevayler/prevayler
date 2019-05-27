package org.prevayler.demos.scalability.prevayler;

import org.prevayler.Transaction;
import org.prevayler.demos.scalability.RecordIterator;

import java.util.Date;

class AllRecordsReplacement<S extends ScalabilitySystem> implements Transaction<S> {

  private static final long serialVersionUID = 6283032417365727408L;
  private final int _records;

  AllRecordsReplacement(int records) {
    _records = records;
  }

  public void executeOn(S system, Date ignored) {
    system.replaceAllRecords(new RecordIterator(_records));
  }
}
