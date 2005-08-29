package org.prevayler.implementation;

import org.prevayler.Transaction;
import org.prevayler.foundation.serialization.Serializer;

import java.util.Date;

class TransactionCapsule extends Capsule {

	private static final long serialVersionUID = 3283271592697928351L;

	public TransactionCapsule(Transaction transaction, Serializer journalSerializer) {
		super(transaction, journalSerializer);
	}

	public TransactionCapsule(byte[] serialized) {
		super(serialized);
	}

	protected void justExecute(Object transaction, Object prevalentSystem, Date executionTime) {
		((Transaction) transaction).executeOn(prevalentSystem, executionTime);
	}

	public Capsule cleanCopy() {
		// TransactionCapsule, unlike TransactionWithQueryCapsule, is completely immutable.
		return this;
	}

}
