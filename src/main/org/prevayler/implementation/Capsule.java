package org.prevayler.implementation;

import org.prevayler.foundation.Chunk;
import org.prevayler.foundation.serialization.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;

public abstract class Capsule implements Serializable {

	private final byte[] _serialized;

	protected Capsule(Object transaction, Serializer journalSerializer) {
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
	 * Get the serialized representation of the transaction. Callers must not modify the returned array.
	 */
	public byte[] serialized() {
		return _serialized;
	}

	/**
	 * Deserialize the contained Transaction or TransactionWithQuery.
	 */
	public Object deserialize(Serializer journalSerializer) {
		try {
			return journalSerializer.readObject(new ByteArrayInputStream(_serialized));
		} catch (Exception exception) {
			throw new Error("Unable to deserialize transaction", exception);
		}
	}

	/**
	 * Execute a freshly deserialized copy of the transaction. This method will synchronize on the prevalentSystem
	 * while running the transaction but after deserializing it.
	 */
	public void executeOn(Object prevalentSystem, Date executionTime, Serializer journalSerializer) {
		Object transaction = deserialize(journalSerializer);

		synchronized (prevalentSystem) {
			justExecute(transaction, prevalentSystem, executionTime);
		}
	}

	/**
	 * Actually execute the Transaction or TransactionWithQuery. The caller
	 * is responsible for synchronizing on the prevalentSystem.
	 */
	protected abstract void justExecute(Object transaction, Object prevalentSystem, Date executionTime);

	/**
	 * Make a clean copy of this capsule that will have its own query result fields.
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
