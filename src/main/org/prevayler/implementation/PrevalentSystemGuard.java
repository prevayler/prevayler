package org.prevayler.implementation;

import org.prevayler.Clock;
import org.prevayler.Query;
import org.prevayler.Transaction;
import org.prevayler.foundation.Cool;
import org.prevayler.foundation.DeepCopier;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.publishing.TransactionSubscriber;
import org.prevayler.implementation.snapshot.GenericSnapshotManager;

import java.io.IOException;
import java.util.Date;

public class PrevalentSystemGuard implements TransactionSubscriber {

	private final Object _prevalentSystem; // All access is synchronized on itself
	private long _systemVersion; // All access is synchronized on "this"
	private boolean _ignoreRuntimeExceptions; // All access is synchronized on "this"

	public PrevalentSystemGuard(Object prevalentSystem, long systemVersion) {
		_prevalentSystem = prevalentSystem;
		_systemVersion = systemVersion;
		_ignoreRuntimeExceptions = false;
	}

	public Object prevalentSystem() {
		return _prevalentSystem;
	}

	public void subscribeTo(TransactionPublisher publisher) throws IOException, ClassNotFoundException {
		long initialTransaction;
		synchronized (this) {
			_ignoreRuntimeExceptions = true;     //During pending transaction recovery (rolling forward), RuntimeExceptions are ignored because they were already thrown and handled during the first transaction execution.
			initialTransaction = _systemVersion + 1;
		}

		publisher.addSubscriber(this, initialTransaction);

		synchronized (this) {
			_ignoreRuntimeExceptions = false;
		}
	}

	public void receive(TransactionTimestamp transactionTimstamp) {
		Transaction transaction = transactionTimstamp.transaction();
		long systemVersion = transactionTimstamp.systemVersion();
		Date executionTime = transactionTimstamp.executionTime();

		synchronized (this) {
			if (systemVersion != _systemVersion + 1) {
				throw new IllegalStateException(
						"Attempted to apply transaction " + systemVersion + " when prevalent system was only at " + _systemVersion);
			}

			_systemVersion = systemVersion;

			try {
				synchronized (_prevalentSystem) {
					transaction.executeOn(_prevalentSystem, executionTime);
				}
			} catch (RuntimeException rx) {
				if (!_ignoreRuntimeExceptions) throw rx;  //TODO Guarantee that transactions received from pending transaction recovery don't ever throw RuntimeExceptions. Maybe use a wrapper for that.
			} finally {
				notifyAll();
			}
		}
	}

	public Object executeQuery(Query sensitiveQuery, Clock clock) throws Exception {
		synchronized (_prevalentSystem) {
			return sensitiveQuery.query(_prevalentSystem, clock.time());
		}
	}

	public void takeSnapshot(GenericSnapshotManager snapshotManager) throws IOException {
		synchronized (this) {
			synchronized (_prevalentSystem) {
				snapshotManager.writeSnapshot(_prevalentSystem, _systemVersion);
			}
		}
	}

	public PrevalentSystemGuard deepCopy(long systemVersion, Serializer snapshotSerializer) throws IOException, ClassNotFoundException {
		synchronized (this) {
			while (_systemVersion < systemVersion) {
				Cool.wait(this);
			}

			if (_systemVersion > systemVersion) {
				throw new IllegalStateException("Already at " + _systemVersion + "; can't go back to " + systemVersion);
			}

			synchronized (_prevalentSystem) {
				return new PrevalentSystemGuard(DeepCopier.deepCopy(_prevalentSystem, snapshotSerializer), _systemVersion);
			}
		}
	}

}
