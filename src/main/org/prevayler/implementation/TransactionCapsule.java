package org.prevayler.implementation;

import org.prevayler.Transaction;
import org.prevayler.foundation.serialization.Serializer;

import java.util.Date;

public class TransactionCapsule extends Capsule {

	public TransactionCapsule(Transaction transaction, Serializer journalSerializer) {
		super(transaction, journalSerializer);
	}

	public TransactionCapsule(byte[] serialized) {
		super(serialized);
	}

	public void executeOn(Object prevalentSystem, Date executionTime, Serializer journalSerializer) {
		Transaction transaction = (Transaction) deserialize(journalSerializer);

		synchronized (prevalentSystem) {
			transaction.executeOn(prevalentSystem, executionTime);
		}
	}

	public Capsule cleanCopy() {
		// TransactionCapsule, unlike TransactionWithQueryCapsule, is completely immutable.
		return this;
	}

}
