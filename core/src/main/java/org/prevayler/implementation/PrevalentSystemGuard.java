package org.prevayler.implementation;

import org.prevayler.Clock;
import org.prevayler.Query;
import org.prevayler.foundation.Cool;
import org.prevayler.foundation.DeepCopier;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.publishing.TransactionSubscriber;
import org.prevayler.implementation.snapshot.GenericSnapshotManager;

import java.io.IOException;
import java.util.Date;

public class PrevalentSystemGuard implements TransactionSubscriber {

	private Object _prevalentSystem; // All access to field is synchronized on "this", and all access to object is synchronized on itself; "this" is always locked before the object
	private long _systemVersion; // All access is synchronized on "this"
	private boolean _ignoreRuntimeExceptions; // All access is synchronized on "this"
	private final Serializer _journalSerializer;

	public PrevalentSystemGuard(Object prevalentSystem, long systemVersion, Serializer journalSerializer) {
		_prevalentSystem = prevalentSystem;
		_systemVersion = systemVersion;
		_ignoreRuntimeExceptions = false;
		_journalSerializer = journalSerializer;
	}

	public Object prevalentSystem() {
        synchronized (this) {
            if (_prevalentSystem == null) {
                throw new Error("Prevayler is no longer allowing access to the prevalent system due to an Error thrown from an earlier transaction.");
            }
            return _prevalentSystem;
        }
	}

	public void subscribeTo(TransactionPublisher publisher) throws IOException, ClassNotFoundException {
		long initialTransaction;
		synchronized (this) {
			_ignoreRuntimeExceptions = true;     //During pending transaction recovery (rolling forward), RuntimeExceptions are ignored because they were already thrown and handled during the first transaction execution.
			initialTransaction = _systemVersion + 1;
		}

		publisher.subscribe(this, initialTransaction);

		synchronized (this) {
			_ignoreRuntimeExceptions = false;
		}
	}

	public void receive(TransactionTimestamp transactionTimestamp) {
		Capsule capsule = transactionTimestamp.capsule();
		long systemVersion = transactionTimestamp.systemVersion();
		Date executionTime = transactionTimestamp.executionTime();

		synchronized (this) {
            if (_prevalentSystem == null) {
                throw new Error("Prevayler is no longer processing transactions due to an Error thrown from an earlier transaction.");
            }

            if (systemVersion != _systemVersion + 1) {
				throw new IllegalStateException(
						"Attempted to apply transaction " + systemVersion + " when prevalent system was only at " + _systemVersion);
			}

			_systemVersion = systemVersion;

			try {
				// Don't synchronize on _prevalentSystem here so that the capsule can deserialize a fresh
				// copy of the transaction without blocking queries.
				capsule.executeOn(_prevalentSystem, executionTime, _journalSerializer);
			} catch (RuntimeException rx) {
				if (!_ignoreRuntimeExceptions) throw rx;  //TODO Guarantee that transactions received from pending transaction recovery don't ever throw RuntimeExceptions. Maybe use a wrapper for that.
            } catch (Error error) {
                _prevalentSystem = null;
                throw error;
            } finally {
				notifyAll();
			}
		}
	}

	public Object executeQuery(Query sensitiveQuery, Clock clock) throws Exception {
        synchronized (this) {
            if (_prevalentSystem == null) {
                throw new Error("Prevayler is no longer processing queries due to an Error thrown from an earlier transaction.");
            }

    		synchronized (_prevalentSystem) {
    			return sensitiveQuery.query(_prevalentSystem, clock.time());
    		}
        }
	}

	public void takeSnapshot(GenericSnapshotManager snapshotManager) throws IOException {
		synchronized (this) {
            if (_prevalentSystem == null) {
                throw new Error("Prevayler is no longer allowing snapshots due to an Error thrown from an earlier transaction.");
            }

			synchronized (_prevalentSystem) {
				snapshotManager.writeSnapshot(_prevalentSystem, _systemVersion);
			}
		}
	}

	public PrevalentSystemGuard deepCopy(long systemVersion, Serializer snapshotSerializer) throws IOException, ClassNotFoundException {
		synchronized (this) {
			while (_systemVersion < systemVersion && _prevalentSystem != null) {
				Cool.wait(this);
			}

            if (_prevalentSystem == null) {
                throw new Error("Prevayler is no longer accepting transactions due to an Error thrown from an earlier transaction.");
            }

			if (_systemVersion > systemVersion) {
				throw new IllegalStateException("Already at " + _systemVersion + "; can't go back to " + systemVersion);
			}

			synchronized (_prevalentSystem) {
				return new PrevalentSystemGuard(DeepCopier.deepCopyParallel(_prevalentSystem, snapshotSerializer), _systemVersion, _journalSerializer);
			}
		}
	}

}
