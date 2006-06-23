// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation;

import org.prevayler.Clock;
import org.prevayler.Prevayler;
import org.prevayler.ReadOnly;
import org.prevayler.Transaction;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.snapshot.SnapshotManager;

public class PrevaylerImpl<T> implements Prevayler<T> {

    private final PrevalentSystemGuard<T> _guard;

    private final Clock _clock;

    private final SnapshotManager<T> _snapshotManager;

    private final TransactionPublisher<T> _publisher;

    private final Serializer<Transaction> _journalSerializer;

    /**
     * Creates a new Prevayler
     * 
     * @param snapshotManager
     *            The SnapshotManager that will be used for reading and writing
     *            snapshot files.
     * @param transactionPublisher
     *            The TransactionPublisher that will be used for publishing
     *            transactions executed with this PrevaylerImpl.
     * @param prevaylerMonitor
     *            The Monitor that will be used to monitor interesting calls to
     *            this PrevaylerImpl.
     * @param journalSerializer
     */
    public PrevaylerImpl(SnapshotManager<T> snapshotManager, TransactionPublisher<T> transactionPublisher, Serializer<Transaction> journalSerializer) {
        _snapshotManager = snapshotManager;

        _guard = _snapshotManager.recoveredPrevalentSystem();

        _publisher = transactionPublisher;
        _clock = _publisher.clock();

        _guard.subscribeTo(_publisher);

        _journalSerializer = journalSerializer;
    }

    public T prevalentSystem() {
        return _guard.prevalentSystem();
    }

    public <R, E extends Exception> R execute(Transaction<? super T, R, E> transaction) throws E {
        if (transaction.getClass().isAnnotationPresent(ReadOnly.class)) {
            return _guard.executeQuery(transaction, _clock);
        } else {
            Capsule<T, R, E> capsule = new Capsule<T, R, E>(transaction, _journalSerializer);
            publish(capsule);
            return capsule.result();
        }
    }

    private <R, E extends Exception> void publish(Capsule<T, R, E> capsule) {
        _publisher.publish(capsule);
    }

    public void takeSnapshot() {
        _guard.takeSnapshot(_snapshotManager);
    }

    public void close() {
        _publisher.close();
    }

}
