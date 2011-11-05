package org.prevayler.demos.scalability.prevayler;

import java.util.Date;

import org.prevayler.Transaction;
import org.prevayler.demos.scalability.RecordIterator;

class AllRecordsReplacement implements Transaction<ScalabilitySystem> {

	private static final long serialVersionUID = 6283032417365727408L;
	private final int _records;

	AllRecordsReplacement(int records) { _records = records; }

	public void executeOn(ScalabilitySystem system, Date ignored) {
		system.replaceAllRecords(new RecordIterator(_records));
	}
}
