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
	private transient Serializer _journalSerializer;
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

		_journalSerializer = journalSerializer;
	}

	public TransactionCapsule(boolean withQuery, byte[] serialized, Serializer journalSerializer) {
		_withQuery = withQuery;
		_serialized = serialized;
		_journalSerializer = journalSerializer;
	}

	public boolean withQuery() {
		return _withQuery;
	}

	public byte[] serialized() {
		return _serialized;
	}

	public void executeOn(Object prevalentSystem, Date executionTime) {
		if (_withQuery) {
			TransactionWithQuery transactionWithQuery = (TransactionWithQuery) deserialize();

			try {
				_queryResult = transactionWithQuery.executeAndQuery(prevalentSystem, executionTime);
			} catch (RuntimeException rx) {
				_queryException = rx;
				throw rx;   //This is necessary because of the rollback feature.
			} catch (Exception ex) {
				_queryException = ex;
			}
		} else {
			Transaction transaction = (Transaction) deserialize();

			transaction.executeOn(prevalentSystem, executionTime);
		}
	}

	public Object deserialize() {
		try {
			return _journalSerializer.readObject(new ByteArrayInputStream(_serialized));
		} catch (Exception exception) {
			throw new Error("Unable to deserialize transaction", exception);
		}
	}

	public Object result() throws Exception {
		if (!_withQuery) throw new IllegalStateException("Was not a TransactionWithQuery");
		if (_queryException != null) throw _queryException;
		return _queryResult;
	}

	public TransactionCapsule cleanCopy() {
		return new TransactionCapsule(_withQuery, _serialized, _journalSerializer);
	}

	/**
	 * Restore the internal journal serializer after deserializing the capsule itself.
	 */
	public TransactionCapsule withSerializer(Serializer journalSerializer) {
		return new TransactionCapsule(_withQuery, _serialized, journalSerializer);
	}

}
