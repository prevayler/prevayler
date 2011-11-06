package org.prevayler.implementation;

import org.prevayler.Transaction;
import org.prevayler.foundation.serialization.Serializer;

import java.io.Serializable;
import java.util.Date;

class TransactionCapsule<P extends Serializable> extends Capsule{

	private static final long serialVersionUID = 3283271592697928351L;

	public TransactionCapsule(Transaction<P> transaction, Serializer journalSerializer, boolean copyBeforeExecuteMode) {
		super(transaction, journalSerializer, copyBeforeExecuteMode);
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
