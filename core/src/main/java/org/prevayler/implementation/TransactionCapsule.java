package org.prevayler.implementation;

import org.prevayler.Transaction;
import org.prevayler.foundation.serialization.Serializer;

import java.util.Date;

class TransactionCapsule<P> extends Capsule<P, Transaction<P>> {

  private static final long serialVersionUID = 3283271592697928351L;

  public TransactionCapsule(Transaction<P> transaction, Serializer journalSerializer, boolean transactionDeepCopyMode) {
    super(transaction, journalSerializer, transactionDeepCopyMode);
  }

  public TransactionCapsule(byte[] serialized) {
    super(serialized);
  }

  protected void justExecute(Transaction<P> transaction, P prevalentSystem, Date executionTime) {
    transaction.executeOn(prevalentSystem, executionTime);
  }

  public Capsule<P, Transaction<P>> cleanCopy() {
    // TransactionCapsule, unlike TransactionWithQueryCapsule, is completely immutable.
    return this;
  }

}
