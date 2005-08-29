package org.prevayler.implementation;

import org.prevayler.TransactionWithQuery;
import org.prevayler.foundation.serialization.Serializer;

import java.util.Date;

class TransactionWithQueryCapsule extends Capsule {

	private static final long serialVersionUID = 78811627002206298L;
	private transient Object _queryResult;
	private transient Exception _queryException;

	public TransactionWithQueryCapsule(TransactionWithQuery transactionWithQuery, Serializer journalSerializer) {
		super(transactionWithQuery, journalSerializer);
	}

	public TransactionWithQueryCapsule(byte[] serialized) {
		super(serialized);
	}

	protected void justExecute(Object transaction, Object prevalentSystem, Date executionTime) {
		try {
			_queryResult = ((TransactionWithQuery) transaction).executeAndQuery(prevalentSystem, executionTime);
		} catch (RuntimeException rx) {
			_queryException = rx;
			throw rx;   //This is necessary because of the rollback feature.
		} catch (Exception ex) {
			_queryException = ex;
		}
	}

	public Object result() throws Exception {
		if (_queryException != null) throw _queryException;
		return _queryResult;
	}

	public Capsule cleanCopy() {
		return new TransactionWithQueryCapsule(serialized());
	}

}
