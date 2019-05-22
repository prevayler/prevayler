//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import org.prevayler.*;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.snapshot.GenericSnapshotManager;

import java.io.File;
import java.io.IOException;

public class PrevaylerImpl<P> implements Prevayler<P> {

  private final PrevalentSystemGuard<P> _guard;
  private final Clock _clock;

  private final GenericSnapshotManager<P> _snapshotManager;

  private final TransactionPublisher<P> _publisher;

  private final Serializer _journalSerializer;

  private boolean _transactionDeepCopyMode;


  /**
   * Creates a new Prevayler
   *
   * @param snapshotManager      The SnapshotManager that will be used for reading and writing snapshot files.
   * @param transactionPublisher The TransactionPublisher that will be used for publishing transactions executed with this PrevaylerImpl.
   * @param journalSerializer
   */
  public PrevaylerImpl(GenericSnapshotManager<P> snapshotManager, TransactionPublisher<P> transactionPublisher,
                       Serializer journalSerializer, boolean transactionDeepCopyMode) throws IOException, ClassNotFoundException {
    _snapshotManager = snapshotManager;

    _guard = _snapshotManager.recoveredPrevalentSystem();

    _publisher = transactionPublisher;
    _clock = _publisher.clock();

    _guard.subscribeTo(_publisher);

    _journalSerializer = journalSerializer;

    _transactionDeepCopyMode = transactionDeepCopyMode;
  }

  public P prevalentSystem() {
    return _guard.prevalentSystem();
  }


  public Clock clock() {
    return _clock;
  }


  public void execute(Transaction<? super P> transaction) {
    publish(new TransactionCapsule<P>(transaction, _journalSerializer, _transactionDeepCopyMode));    //TODO Optimization: The Censor can use the actual given transaction if it is Immutable instead of deserializing a new one from the byte array, even if "_transactionDeepCopyMode" is "true"
  }


  private void publish(Capsule<? super P, ? extends TransactionBase> capsule) {
    _publisher.publish(capsule);
  }


  public <R> R execute(Query<? super P, R> sensitiveQuery) throws Exception {
    return _guard.executeQuery(sensitiveQuery, clock());
  }


  public <R> R execute(TransactionWithQuery<? super P, R> transactionWithQuery) throws Exception {
    TransactionWithQueryCapsule<? super P, R> capsule = new TransactionWithQueryCapsule<P, R>(transactionWithQuery, _journalSerializer, _transactionDeepCopyMode);
    publish(capsule);
    return capsule.result();
  }


  public <R> R execute(SureTransactionWithQuery<? super P, R> sureTransactionWithQuery) {
    try {
      return execute(sureTransactionWithQuery);
    } catch (RuntimeException runtime) {
      throw runtime;
    } catch (Exception checked) {
      throw new RuntimeException("Unexpected Exception thrown.", checked);
    }
  }


  public File takeSnapshot() throws Exception {
    return _guard.takeSnapshot(_snapshotManager);
  }


  public void close() throws IOException {
    _publisher.close();
  }

}
