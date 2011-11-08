package org.prevayler.implementation;

import org.prevayler.Transaction;
import org.prevayler.foundation.Chunk;
import org.prevayler.foundation.serialization.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;

public abstract class Capsule implements Serializable {

	private final byte[] _serialized;
	private transient Object _transaction = null;
	
	protected Capsule(Object transaction, Serializer journalSerializer, boolean transactionDeepCopyMode) {
		if(transactionDeepCopyMode == false){
			_transaction = transaction;
		}
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			journalSerializer.writeObject(bytes, transaction);
			_serialized = bytes.toByteArray();
		} catch (Exception exception) {
			throw new Error("Unable to serialize transaction", exception);
		}
	}

	protected Capsule(byte[] serialized) {
		_serialized = serialized;
	}

	/**
	 * Gets the serialized representation of the transaction. Callers must not modify the returned array.
	 */
	public byte[] serialized() {
		return _serialized;
	}

	/**
	 * Deserializes the contained Transaction or TransactionWithQuery.
	 */
	public Object deserialize(Serializer journalSerializer) {
		try {
			return journalSerializer.readObject(new ByteArrayInputStream(_serialized));
		} catch (Exception exception) {
			throw new Error("Unable to deserialize transaction", exception);
		}
	}

	/**
	 * Executes a freshly deserialized copy of the transaction if this is being called via the royal food taster or if <code>configureTransactionDeepCopy</code> wasn't set to <code>false</code> on your <code>PrevaylerFactory</code>. Otherwise, this will execute the transaction directly. The execution will synchronize on the prevalentSystem.
	 */
	public void executeOn(Object prevalentSystem, Date executionTime, Serializer journalSerializer, boolean guaranteeTransactionDeepCopy) {
		Object transaction;
		if(guaranteeTransactionDeepCopy || _transaction == null){
			transaction = deserialize(journalSerializer);
		}
		else{
			transaction = _transaction;
		}
		
		synchronized (prevalentSystem) {
			justExecute(transaction, prevalentSystem, executionTime);
		}
	}

	/**
	 * Actually executes the Transaction or TransactionWithQuery. The caller
	 * is responsible for synchronizing on the prevalentSystem.
	 */
	protected abstract void justExecute(Object transaction, Object prevalentSystem, Date executionTime);
		
	/**
	 * Makes a clean copy of this capsule that will have its own query result fields.
	 */
	public abstract Capsule cleanCopy();

	Chunk toChunk() {
		Chunk chunk = new Chunk(_serialized);
		chunk.setParameter("withQuery", String.valueOf(this instanceof TransactionWithQueryCapsule));
		return chunk;
	}

	static Capsule fromChunk(Chunk chunk) {
		boolean withQuery = Boolean.valueOf(chunk.getParameter("withQuery")).booleanValue();
		if (withQuery) {
			return new TransactionWithQueryCapsule(chunk.getBytes());
		} else {
			return new TransactionCapsule(chunk.getBytes());
		}
	}

}
