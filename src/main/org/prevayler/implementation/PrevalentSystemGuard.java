package org.prevayler.implementation;

import org.prevayler.Query;
import org.prevayler.Transaction;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.publishing.TransactionSubscriber;
import org.prevayler.implementation.snapshot.GenericSnapshotManager;

import java.io.IOException;
import java.util.Date;

public class PrevalentSystemGuard implements TransactionSubscriber {

	private final Object _prevalentSystem;
	private long _systemVersion;
	private boolean _ignoreRuntimeExceptions;

	public PrevalentSystemGuard(Object prevalentSystem, long systemVersion) {
		_prevalentSystem = prevalentSystem;
		_systemVersion = systemVersion;
	}

	public Object prevalentSystem() {
		return _prevalentSystem;
	}

	public void subscribeTo(TransactionPublisher publisher) throws IOException, ClassNotFoundException {
		_ignoreRuntimeExceptions = true;     //During pending transaction recovery (rolling forward), RuntimeExceptions are ignored because they were already thrown and handled during the first transaction execution.
		publisher.addSubscriber(this, _systemVersion + 1);
		_ignoreRuntimeExceptions = false;
	}

	public void receive(Transaction transaction, Date executionTime) {
		synchronized (_prevalentSystem) {
			_systemVersion++;
			try {
				transaction.executeOn(_prevalentSystem, executionTime);
			} catch (RuntimeException rx) {
				if (!_ignoreRuntimeExceptions) throw rx;  //TODO Guarantee that transactions received from pending transaction recovery don't ever throw RuntimeExceptions. Maybe use a wrapper for that.
			}
		}
	}

	public Object executeQuery(Query sensitiveQuery, Date executionTime) throws Exception {
		synchronized (_prevalentSystem) {
			return sensitiveQuery.query(_prevalentSystem, executionTime);
		}
	}

	public void takeSnapshot(GenericSnapshotManager snapshotManager) throws IOException {
		synchronized (_prevalentSystem) {
			snapshotManager.writeSnapshot(_prevalentSystem, _systemVersion);
		}
	}

}
