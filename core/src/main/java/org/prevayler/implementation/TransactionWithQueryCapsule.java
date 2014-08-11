package org.prevayler.implementation;

import org.prevayler.TransactionWithQuery;
import org.prevayler.foundation.serialization.Serializer;

import java.util.Date;

class TransactionWithQueryCapsule<P, R> extends Capsule {

  private static final long serialVersionUID = 78811627002206298L;
  private transient R _queryResult;
  private transient Exception _queryException;

  public TransactionWithQueryCapsule(TransactionWithQuery<? super P, R> transactionWithQuery, Serializer journalSerializer, boolean transactionDeepCopyMode) {
    super(transactionWithQuery, journalSerializer, transactionDeepCopyMode);
  }

  public TransactionWithQueryCapsule(byte[] serialized) {
    super(serialized);
  }

  protected void justExecute(Object transaction, Object prevalentSystem, Date executionTime) {
    try {
      _queryResult = ((TransactionWithQuery<P, R>) transaction).executeAndQuery((P) prevalentSystem, executionTime);
    } catch (RuntimeException rx) {
      _queryException = rx;
      throw rx;   //This is necessary because of the rollback feature.
    } catch (Exception ex) {
      _queryException = ex;
    }
  }

  public R result() throws Exception {
    if (_queryException != null) throw _queryException;
    return _queryResult;
  }

  public Capsule cleanCopy() {
    return new TransactionWithQueryCapsule<P, R>(serialized());
  }

}
