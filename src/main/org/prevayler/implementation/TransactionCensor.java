package org.prevayler.implementation;

import org.prevayler.Clock;
import org.prevayler.Transaction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TransactionCensor implements TransactionPublisher {

	private final Object _king;
	private Object _royalFoodTaster;
	private final SnapshotManager _snapshotManager;
	private final TransactionPublisher _delegate;

	public TransactionCensor(Object king, SnapshotManager snapshotManager, TransactionPublisher delegate) {
		_king = king;
		_snapshotManager = snapshotManager;
		_delegate = delegate;
	}

	public Clock clock() {
		return _delegate.clock();
	}

	public void addSubscriber(TransactionSubscriber subscriber,	long initialTransaction) throws IOException, ClassNotFoundException {
		_delegate.addSubscriber(subscriber, initialTransaction);
	}

	public void publish(Transaction transaction) {
		try {
			transaction.executeOn(royalFoodTaster(), clock().time());
			//TODO Make sure the transaction has the same timestamp when executed by the delegate.
		} catch (RuntimeException rx) {
			letTheFoodTasterDie();
			throw rx;
		}
		_delegate.publish(transaction);
	}

	private void letTheFoodTasterDie() {
		_royalFoodTaster = null;
	}

	private Object royalFoodTaster() {
		if (_royalFoodTaster == null) produceNewFoodTaster();
		return _royalFoodTaster; 
	}

	private void produceNewFoodTaster() {
		try {
			// TODO Optimization: use a sort of producer-consumer stream so that serialization as deserialization can occur in parallel, avoiding the need for RAM for this array with the whole serialized system. 
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			synchronized (_king) { _snapshotManager.writeSnapshot(_king, out); }
			_royalFoodTaster = _snapshotManager.readSnapshot(new ByteArrayInputStream(out.toByteArray()));
		} catch (Exception e) {
			throw new RuntimeException("Could not rollback.");
		}
	}

}
