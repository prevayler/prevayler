package org.prevayler.demos.scalability.prevayler;

import java.util.Date;

import org.prevayler.Transaction;
import org.prevayler.demos.scalability.RecordIterator;

class AllRecordsReplacement implements Transaction {

	private static final long serialVersionUID = 6283032417365727408L;
	private final int _records;

	AllRecordsReplacement(int records) { _records = records; }

	public void executeOn(Object system, Date ignored) {
		((ScalabilitySystem)system).replaceAllRecords(new RecordIterator(_records));
	}
}
