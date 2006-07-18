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
import org.prevayler.GenericTransaction;
import org.prevayler.Listener;
import org.prevayler.PrevalenceContext;
import org.prevayler.Safety.Locking;
import org.prevayler.foundation.DeepCopier;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.publishing.TransactionSubscriber;

import java.util.Date;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PrevalentSystemGuard<S> implements TransactionSubscriber<S> {

    // All access to field is synchronized on _rwlock, and all write access to
    // object is synchronized on itself; _rwlock is always locked before the
    // object
    private S _prevalentSystem;

    // All access is synchronized on _rwlock
    private long _systemVersion;

    private final Serializer<GenericTransaction> _journalSerializer;

    private final Listeners _listeners;

    private final ReadWriteLock _rwlock = new ReentrantReadWriteLock();

    private final Condition _deepCopyPipelineFlush = _rwlock.writeLock().newCondition();

    private final boolean _isKing;

    public PrevalentSystemGuard(S prevalentSystem, long systemVersion, Serializer<GenericTransaction> journalSerializer) {
        this(prevalentSystem, systemVersion, journalSerializer, true);
    }

    private PrevalentSystemGuard(S prevalentSystem, long systemVersion, Serializer<GenericTransaction> journalSerializer, boolean isKing) {
        _prevalentSystem = prevalentSystem;
        _systemVersion = systemVersion;
        _journalSerializer = journalSerializer;
        _listeners = new Listeners();
        _isKing = isKing;
    }

    public void subscribeTo(TransactionPublisher<S> publisher) {
        long initialTransaction;

        _rwlock.writeLock().lock();
        try {
            initialTransaction = _systemVersion + 1;
        } finally {
            _rwlock.writeLock().unlock();
        }

        publisher.subscribe(this, initialTransaction);
    }

    public <R, E extends Exception> void receive(TransactionTimestamp<S, R, E> transactionTimestamp) {
        TransactionCapsule<S, R, E> capsule = transactionTimestamp.capsule();
        long systemVersion = transactionTimestamp.systemVersion();
        Date executionTime = transactionTimestamp.executionTime();
        GenericTransaction<? super S, R, E> transaction = capsule.deserialize(_journalSerializer, _isKing);
        Locking locking = SafetyCache.getLocking(transaction);
        PrevalenceContext<S> prevalenceContext;

        _rwlock.writeLock().lock();
        try {
            if (_prevalentSystem == null) {
                throw new ErrorInEarlierTransactionError();
            }

            if (systemVersion != _systemVersion + 1) {
                throw new IllegalStateException("Attempted to apply transaction " + systemVersion + " when prevalent system was only at " + _systemVersion);
            }

            _systemVersion = systemVersion;

            prevalenceContext = new PrevalenceContext<S>(_prevalentSystem, executionTime, systemVersion, false);

            try {
                if (locking.compareTo(Locking.PREVALENT_SYSTEM) >= 0) {
                    synchronized (_prevalentSystem) {
                        capsule.execute(transaction, prevalenceContext);
                    }
                } else {
                    capsule.execute(transaction, prevalenceContext);
                }
            } catch (Error error) {
                _prevalentSystem = null;
                throw error;
            } finally {
                _deepCopyPipelineFlush.signalAll();
            }
        } finally {
            _rwlock.writeLock().unlock();
        }

        dispatchEvents(prevalenceContext);
    }

    public <R, E extends Exception> R executeQuery(GenericTransaction<? super S, R, E> readOnlyTransaction, Clock clock) throws E {
        Locking locking = SafetyCache.getLocking(readOnlyTransaction);
        PrevalenceContext<S> prevalenceContext;
        R result = null;

        Lock whichLock = locking.compareTo(Locking.EXCLUSIVE) >= 0 ? _rwlock.writeLock() : _rwlock.readLock();

        whichLock.lock();
        try {
            if (_prevalentSystem == null) {
                throw new ErrorInEarlierTransactionError();
            }

            prevalenceContext = new PrevalenceContext<S>(_prevalentSystem, clock.time(), _systemVersion, true);

            if (locking.compareTo(Locking.PREVALENT_SYSTEM) >= 0) {
                synchronized (_prevalentSystem) {
                    result = readOnlyTransaction.executeOn(prevalenceContext);
                }
            } else if (locking.compareTo(Locking.SHARED) >= 0) {
                result = readOnlyTransaction.executeOn(prevalenceContext);
            }
        } finally {
            whichLock.unlock();
        }

        if (locking.compareTo(Locking.SHARED) < 0) {
            result = readOnlyTransaction.executeOn(prevalenceContext);
        }

        dispatchEvents(prevalenceContext);
        return result;
    }

    private void dispatchEvents(PrevalenceContext<S> prevalenceContext) {
        for (Object event : prevalenceContext.events()) {
            _listeners.dispatch(event);
        }
    }

    public PrevalentSystemGuard<S> deepCopy(long systemVersion, Serializer<S> snapshotSerializer) {
        // Even though this is only reading the prevalent system, not writing,
        // it requires the exclusive lock because of the pipeline flushing
        // behavior.
        _rwlock.writeLock().lock();
        try {
            while (_systemVersion < systemVersion && _prevalentSystem != null) {
                _deepCopyPipelineFlush.awaitUninterruptibly();
            }

            if (_prevalentSystem == null) {
                throw new ErrorInEarlierTransactionError();
            }

            if (_systemVersion > systemVersion) {
                throw new IllegalStateException("Already at " + _systemVersion + "; can't go back to " + systemVersion);
            }

            synchronized (_prevalentSystem) {
                return new PrevalentSystemGuard<S>(DeepCopier.deepCopyParallel(_prevalentSystem, snapshotSerializer), _systemVersion, _journalSerializer, false);
            }
        } finally {
            _rwlock.writeLock().unlock();
        }
    }

    public <E> void addListener(Class<E> eventClass, Listener<? super E> listener) {
        _listeners.addListener(eventClass, listener);
    }

    public <E> void removeListener(Class<E> eventClass, Listener<? super E> listener) {
        _listeners.removeListener(eventClass, listener);
    }

}
