package org.prevayler.implementation;

import org.prevayler.Transaction;
import org.prevayler.TransactionWithQuery;
import org.prevayler.foundation.serialization.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;

public class TransactionCapsule implements Serializable {

	private final boolean _withQuery;
	private final byte[] _serialized;
	private transient Object _queryResult;
	private transient Exception _queryException;

	public TransactionCapsule(Transaction transaction, Serializer journalSerializer) {
		this(false, transaction, journalSerializer);
	}

	public TransactionCapsule(TransactionWithQuery transactionWithQuery, Serializer journalSerializer) {
		this(true, transactionWithQuery, journalSerializer);
	}

	private TransactionCapsule(boolean withQuery, Object transaction, Serializer journalSerializer) {
		_withQuery = withQuery;

		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			journalSerializer.writeObject(bytes, transaction);
			_serialized = bytes.toByteArray();
		} catch (Exception exception) {
			throw new Error("Unable to serialize transaction", exception);
		}
	}

	public TransactionCapsule(boolean withQuery, byte[] serialized) {
		_withQuery = withQuery;
		_serialized = serialized;
	}

	public boolean withQuery() {
		return _withQuery;
	}

	public byte[] serialized() {
		return _serialized;
	}

	/**
	 * Execute a freshly deserialized copy of the transaction. This method will synchronize on the prevalentSystem
	 * while running the transaction but after deserializing it.
	 */
	public void executeOn(Object prevalentSystem, Date executionTime, Serializer journalSerializer) {
		if (_withQuery) {
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
		} else {
			Transaction transaction = (Transaction) deserialize(journalSerializer);

			synchronized (prevalentSystem) {
				transaction.executeOn(prevalentSystem, executionTime);
			}
		}
	}

	public Object deserialize(Serializer journalSerializer) {
		try {
			return journalSerializer.readObject(new ByteArrayInputStream(_serialized));
		} catch (Exception exception) {
			throw new Error("Unable to deserialize transaction", exception);
		}
	}

	public Object result() throws Exception {
		if (!_withQuery) throw new IllegalStateException("Was not a TransactionWithQuery");
		if (_queryException != null) throw _queryException;
		return _queryResult;
	}

	/**
	 * Make a clean copy of this capsule that will have its own query result fields.
	 */
	public TransactionCapsule cleanCopy() {
		return new TransactionCapsule(_withQuery, _serialized);
	}

}
