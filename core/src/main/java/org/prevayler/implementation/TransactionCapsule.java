package org.prevayler.implementation;

import org.prevayler.Transaction;
import org.prevayler.foundation.serialization.Serializer;

import java.util.Date;

class TransactionCapsule<P> extends Capsule{

	private static final long serialVersionUID = 3283271592697928351L;

	public TransactionCapsule(Transaction<? super P> transaction, Serializer journalSerializer, boolean transactionDeepCopyMode) {
		super(transaction, journalSerializer, transactionDeepCopyMode);
	}

	public TransactionCapsule(byte[] serialized) {
		super(serialized);
	}

	protected void justExecute(Object transaction, Object prevalentSystem, Date executionTime) {
		((Transaction<P>) transaction).executeOn((P)prevalentSystem, executionTime);
	}

	public Capsule cleanCopy() {
		// TransactionCapsule, unlike TransactionWithQueryCapsule, is completely immutable.
		return this;
	}
	
}
