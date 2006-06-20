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
import org.prevayler.Query;
import org.prevayler.Transaction;
import org.prevayler.foundation.Cool;
import org.prevayler.foundation.DeepCopier;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.publishing.TransactionSubscriber;
import org.prevayler.implementation.snapshot.SnapshotManager;

import java.util.Date;

public class PrevalentSystemGuard<T> implements TransactionSubscriber<T> {

    // All access to field is synchronized on "this", and all access to object
    // is synchronized on itself; "this" is always locked before the object
    private T _prevalentSystem;

    // All access is synchronized on "this"
    private long _systemVersion;

    // All access is synchronized on "this"
    private boolean _ignoreRuntimeExceptions;

    private final Serializer<Transaction> _journalSerializer;

    public PrevalentSystemGuard(T prevalentSystem, long systemVersion, Serializer<Transaction> journalSerializer) {
        _prevalentSystem = prevalentSystem;
        _systemVersion = systemVersion;
        _ignoreRuntimeExceptions = false;
        _journalSerializer = journalSerializer;
    }

    public T prevalentSystem() {
        synchronized (this) {
            if (_prevalentSystem == null) {
                throw new ErrorInEarlierTransactionError("Prevayler is no longer allowing access to the prevalent system due to an Error thrown from an earlier transaction.");
            }
            return _prevalentSystem;
        }
    }

    public void subscribeTo(TransactionPublisher<T> publisher) {
        long initialTransaction;
        synchronized (this) {
            _ignoreRuntimeExceptions = true; // During pending transaction
            // recovery (rolling forward),
            // RuntimeExceptions are ignored
            // because they were already
            // thrown and handled during the
            // first transaction execution.
            initialTransaction = _systemVersion + 1;
        }

        publisher.subscribe(this, initialTransaction);

        synchronized (this) {
            _ignoreRuntimeExceptions = false;
        }
    }

    public <R, E extends Exception> void receive(TransactionTimestamp<T, R, E> transactionTimestamp) {
        Capsule<T, R, E> capsule = transactionTimestamp.capsule();
        long systemVersion = transactionTimestamp.systemVersion();
        Date executionTime = transactionTimestamp.executionTime();

        synchronized (this) {
            if (_prevalentSystem == null) {
                throw new ErrorInEarlierTransactionError("Prevayler is no longer processing transactions due to an Error thrown from an earlier transaction.");
            }

            if (systemVersion != _systemVersion + 1) {
                throw new IllegalStateException("Attempted to apply transaction " + systemVersion + " when prevalent system was only at " + _systemVersion);
            }

            _systemVersion = systemVersion;

            try {
                Transaction<? super T, R, E> transaction = capsule.deserialize(_journalSerializer);

                synchronized (_prevalentSystem) {
                    capsule.execute(transaction, _prevalentSystem, executionTime);
                }
            } catch (RuntimeException rx) {
                if (!_ignoreRuntimeExceptions)
                    throw rx;
            } catch (Error error) {
                _prevalentSystem = null;
                throw error;
            } finally {
                notifyAll();
            }
        }
    }

    public <R, E extends Exception> R executeQuery(Query<? super T, R, E> sensitiveQuery, Clock clock) throws E {
        synchronized (this) {
            if (_prevalentSystem == null) {
                throw new ErrorInEarlierTransactionError("Prevayler is no longer processing queries due to an Error thrown from an earlier transaction.");
            }

            synchronized (_prevalentSystem) {
                return sensitiveQuery.query(_prevalentSystem, clock.time());
            }
        }
    }

    public void takeSnapshot(SnapshotManager<T> snapshotManager) {
        synchronized (this) {
            if (_prevalentSystem == null) {
                throw new ErrorInEarlierTransactionError("Prevayler is no longer allowing snapshots due to an Error thrown from an earlier transaction.");
            }

            synchronized (_prevalentSystem) {
                snapshotManager.writeSnapshot(_prevalentSystem, _systemVersion);
            }
        }
    }

    public PrevalentSystemGuard<T> deepCopy(long systemVersion, Serializer<T> snapshotSerializer) {
        synchronized (this) {
            while (_systemVersion < systemVersion && _prevalentSystem != null) {
                Cool.wait(this);
            }

            if (_prevalentSystem == null) {
                throw new ErrorInEarlierTransactionError("Prevayler is no longer accepting transactions due to an Error thrown from an earlier transaction.");
            }

            if (_systemVersion > systemVersion) {
                throw new IllegalStateException("Already at " + _systemVersion + "; can't go back to " + systemVersion);
            }

            synchronized (_prevalentSystem) {
                return new PrevalentSystemGuard<T>(DeepCopier.deepCopyParallel(_prevalentSystem, snapshotSerializer), _systemVersion, _journalSerializer);
            }
        }
    }

}
