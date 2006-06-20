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
import org.prevayler.Query;
import org.prevayler.SureTransactionWithQuery;
import org.prevayler.Transaction;
import org.prevayler.TransactionWithQuery;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.snapshot.SnapshotManager;

import java.io.IOException;

public class PrevaylerImpl<T> implements Prevayler<T> {

    private final PrevalentSystemGuard<T> _guard;

    private final Clock _clock;

    private final SnapshotManager _snapshotManager;

    private final TransactionPublisher _publisher;

    private final Serializer _journalSerializer;

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
    public PrevaylerImpl(SnapshotManager snapshotManager, TransactionPublisher transactionPublisher, Serializer journalSerializer) {
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

    public Clock clock() {
        return _clock;
    }

    public void execute(Transaction transaction) {
        publish(new TransactionCapsule(transaction, _journalSerializer));
    }

    private void publish(Capsule capsule) {
        _publisher.publish(capsule);
    }

    public <R, E extends Exception> R execute(Query<T, R, E> sensitiveQuery) throws E {
        return _guard.executeQuery(sensitiveQuery, clock());
    }

    public <R, E extends Exception> R execute(TransactionWithQuery<T, R, E> transactionWithQuery) throws E {
        TransactionWithQueryCapsule<T, R, E> capsule = new TransactionWithQueryCapsule<T, R, E>(transactionWithQuery, _journalSerializer);
        publish(capsule);
        return capsule.result();
    }

    public Object execute(SureTransactionWithQuery sureTransactionWithQuery) {
        try {
            return execute((TransactionWithQuery) sureTransactionWithQuery);
        } catch (RuntimeException runtime) {
            throw runtime;
        } catch (Exception checked) {
            throw new ImpossibleError("SureTransactionWithQuery cannot throw checked exceptions", checked);
        }
    }

    public void takeSnapshot() {
        _guard.takeSnapshot(_snapshotManager);
    }

    public void close() throws IOException {
        _publisher.close();
    }

}
