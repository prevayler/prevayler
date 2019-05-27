package org.prevayler.implementation;

import org.prevayler.TransactionWithQuery;
import org.prevayler.foundation.serialization.Serializer;

import java.util.Date;

class TransactionWithQueryCapsule<P, R> extends Capsule<P, TransactionWithQuery<? super P, R>> {

  private static final long serialVersionUID = 78811627002206298L;
  private transient R _queryResult;
  private transient Exception _queryException;

  public TransactionWithQueryCapsule(TransactionWithQuery<? super P, R> transactionWithQuery, Serializer journalSerializer, boolean transactionDeepCopyMode) {
    super(transactionWithQuery, journalSerializer, transactionDeepCopyMode);
  }

  public TransactionWithQueryCapsule(byte[] serialized) {
    super(serialized);
  }

  protected void justExecute(TransactionWithQuery<? super P, R> transaction, P prevalentSystem, Date executionTime) {
    try {
      _queryResult = transaction.executeAndQuery(prevalentSystem, executionTime);
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

  public Capsule<P, TransactionWithQuery<? super P, R>> cleanCopy() {
    return new TransactionWithQueryCapsule<P, R>(serialized());
  }

}
