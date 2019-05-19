package org.prevayler.implementation;

import org.prevayler.TransactionBase;
import org.prevayler.foundation.Chunk;
import org.prevayler.foundation.serialization.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;

public abstract class Capsule<P, T extends TransactionBase> implements Serializable {

  private static final long serialVersionUID = -8253915186873598285L;
  private final byte[] _serialized;
  private transient T _directTransaction = null;

  protected Capsule(T transaction, Serializer journalSerializer, boolean transactionDeepCopyMode) {
    if (transactionDeepCopyMode == false) {
      _directTransaction = transaction;
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
  @SuppressWarnings("unchecked")
  public T deserialize(Serializer journalSerializer) {
    try {
      return (T) journalSerializer.readObject(new ByteArrayInputStream(_serialized));
    } catch (Exception exception) {
      throw new Error("Unable to deserialize transaction", exception);
    }
  }

  /**
   * Executes a freshly deserialized copy of the transaction by default. If <code>configureTransactionDeepCopy</code> was set to <code>true</code> on your <code>PrevaylerFactory</code>, this will execute the transaction directly. The execution will synchronize on the prevalentSystem.
   */
  public void executeOn(P prevalentSystem, Date executionTime, Serializer journalSerializer) {
    T transaction;
    if (_directTransaction != null) {
      transaction = _directTransaction;
    } else {
      transaction = deserialize(journalSerializer);
    }

    synchronized (prevalentSystem) {
      justExecute(transaction, prevalentSystem, executionTime);
    }
  }

  /**
   * Actually executes the Transaction or TransactionWithQuery. The caller
   * is responsible for synchronizing on the prevalentSystem.
   */
  protected abstract void justExecute(T transaction, P prevalentSystem, Date executionTime);

  /**
   * Makes a clean copy of this capsule that will have its own query result fields.
   */
  public abstract Capsule<P, T> cleanCopy();

  Chunk toChunk() {
    Chunk chunk = new Chunk(_serialized);
    chunk.setParameter("withQuery", String.valueOf(this instanceof TransactionWithQueryCapsule));
    return chunk;
  }

  static <P> Capsule<P, ? extends TransactionBase> fromChunk(Chunk chunk) {
    boolean withQuery = Boolean.valueOf(chunk.getParameter("withQuery")).booleanValue();
    if (withQuery) {
      return new TransactionWithQueryCapsule<P, Object>(chunk.getBytes());
    } else {
      return new TransactionCapsule<P>(chunk.getBytes());
    }
  }

}
