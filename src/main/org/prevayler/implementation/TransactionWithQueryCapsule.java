package org.prevayler.implementation;

import org.prevayler.TransactionWithQuery;
import org.prevayler.foundation.serialization.Serializer;

import java.util.Date;

public class TransactionWithQueryCapsule extends Capsule {

	private transient Object _queryResult;
	private transient Exception _queryException;

	public TransactionWithQueryCapsule(TransactionWithQuery transactionWithQuery, Serializer journalSerializer) {
		super(transactionWithQuery, journalSerializer);
	}

	public TransactionWithQueryCapsule(byte[] serialized) {
		super(serialized);
	}

	public void executeOn(Object prevalentSystem, Date executionTime, Serializer journalSerializer) {
		TransactionWithQuery transactionWithQuery = (TransactionWithQuery) deserialize(journalSerializer);

		try {
			synchronized (prevalentSystem) {
				_queryResult = transactionWithQuery.executeAndQuery(prevalentSystem, executionTime);
			}
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
