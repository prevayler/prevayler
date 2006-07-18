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

import org.prevayler.*;
import org.prevayler.Safety.*;
import org.prevayler.foundation.serialization.*;
import org.prevayler.implementation.publishing.*;
import org.prevayler.implementation.snapshot.*;

public class PrevaylerImpl<S> implements Prevayler<S> {

    private final PrevalentSystemGuard<S> _guard;

    private final Clock _clock;

    private final SnapshotManager<S> _snapshotManager;

    private final TransactionPublisher<S> _publisher;

    private final Serializer<GenericTransaction> _journalSerializer;

    private final boolean _withRollbackForBackwardCompatibilty;

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
    public PrevaylerImpl(SnapshotManager<S> snapshotManager, TransactionPublisher<S> transactionPublisher, Serializer<GenericTransaction> journalSerializer, boolean withRollbackForBackwardCompatibilty) {
        _snapshotManager = snapshotManager;

        _guard = _snapshotManager.recoveredPrevalentSystem();

        _publisher = transactionPublisher;
        _clock = _publisher.clock();

        _guard.subscribeTo(_publisher);

        _journalSerializer = journalSerializer;

        _withRollbackForBackwardCompatibilty = withRollbackForBackwardCompatibilty;
    }

    public <R, E extends Exception> R execute(GenericTransaction<? super S, R, E> transaction) throws E {
        requireSafetyAnnotation(transaction);
        Journaling journaling = SafetyCache.getJournaling(transaction);
        if (journaling.compareTo(Journaling.PERSISTENT) < 0) {
            return _guard.executeQuery(transaction, _clock);
        } else {
            TransactionCapsule<S, R, E> capsule = new TransactionCapsule<S, R, E>(transaction, _journalSerializer);
            publish(capsule);
            return capsule.result();
        }
    }

    private <R, E extends Exception> void publish(TransactionCapsule<S, R, E> capsule) {
        _publisher.publish(capsule);
    }

    public void takeSnapshot() {
        execute(new SnapshotQuery<S>(_snapshotManager));
    }

    public void close() {
        _publisher.close();
    }

    public <E> void addListener(Class<E> eventClass, Listener<? super E> listener) {
        _guard.addListener(eventClass, listener);
    }

    public <E> void removeListener(Class<E> eventClass, Listener<? super E> listener) {
        _guard.removeListener(eventClass, listener);
    }

    @SuppressWarnings("deprecation") public S prevalentSystem() {
        return execute(new SystemQuery<S>());
    }

    @SuppressWarnings("deprecation") public Clock clock() {
        return new QueryingClock(this);
    }

    @SuppressWarnings("deprecation") public void execute(org.prevayler.Transaction<S> transaction) {
        disallowSafetyAnnotation(transaction);
        execute(_withRollbackForBackwardCompatibilty ? new TransactionWrapperWithRollback<S>(transaction) : new TransactionWrapperWithoutRollback<S>(transaction));
    }

    @SuppressWarnings("deprecation") public <R, E extends Exception> R execute(org.prevayler.Query<S, R, E> sensitiveQuery) throws E {
        disallowSafetyAnnotation(sensitiveQuery);
        return execute(new QueryWrapper<S, R, E>(sensitiveQuery));
    }

    @SuppressWarnings("deprecation") public <R, E extends Exception> R execute(org.prevayler.TransactionWithQuery<S, R, E> transactionWithQuery) throws E {
        disallowSafetyAnnotation(transactionWithQuery);
        return execute(_withRollbackForBackwardCompatibilty ? new TransactionWithQueryWrapperWithRollback<S, R, E>(transactionWithQuery) : new TransactionWithQueryWrapperWithoutRollback<S, R, E>(transactionWithQuery));
    }

    @SuppressWarnings("deprecation") public <R> R execute(org.prevayler.SureTransactionWithQuery<S, R> sureTransactionWithQuery) {
        disallowSafetyAnnotation(sureTransactionWithQuery);
        return execute(_withRollbackForBackwardCompatibilty ? new TransactionWithQueryWrapperWithRollback<S, R, RuntimeException>(sureTransactionWithQuery) : new TransactionWithQueryWrapperWithoutRollback<S, R, RuntimeException>(sureTransactionWithQuery));
    }

    private static void requireSafetyAnnotation(Object object) {
        if (!SafetyCache.isAnnotated(object)) {
            throw new IllegalArgumentException("@Safety is required for implementations of GenericTransaction");
        }
    }

    private static void disallowSafetyAnnotation(Object object) {
        if (SafetyCache.isAnnotated(object)) {
            throw new IllegalArgumentException("@Safety only applies to implementations of GenericTransaction");
        }
    }

}
