package org.prevayler.implementation.publishing.censorship;

import org.prevayler.Transaction;
import org.prevayler.implementation.snapshot.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;

public class StrictTransactionCensor implements TransactionCensor {

	private final Object _king;
	private Object _royalFoodTaster;
	private final SnapshotManager _snapshotManager;


	public StrictTransactionCensor(SnapshotManager snapshotManager) {
		_snapshotManager = snapshotManager;
		_king = _snapshotManager.recoveredPrevalentSystem();
		//The _royalFoodTaster cannot be initialized here, or else the pending transactions will not be applied to it.
	}

	public void approve(Transaction transaction, Date executionTime) throws RuntimeException, Error {
		try {
			transaction.executeOn(royalFoodTaster(), executionTime);
		} catch (RuntimeException rx) {
			letTheFoodTasterDie();
			throw rx;
		} catch (Error error) {
			letTheFoodTasterDie();
			throw error;
		}
	}

	private void letTheFoodTasterDie() {
		_royalFoodTaster = null; // Producing the new food taster now could avoid the next transaction having to wait for its lazy evaluation. Trying to serialize the whole system in RAM right after an OutOfMemoryError has been thrown, for example, isn't a very good idea, though. In that case, there probably will not even be a next transaction...  ;)
	}

	private Object royalFoodTaster() {
		if (_royalFoodTaster == null) produceNewFoodTaster();
		return _royalFoodTaster;
	}

	private void produceNewFoodTaster() {
		try {
			// TODO Optimization: use some sort of producer-consumer stream so that serialization and deserialization can occur in parallel, avoiding the need for RAM for this array with the whole serialized system. 
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			synchronized (_king) { _snapshotManager.writeSnapshot(_king, out); }
			_royalFoodTaster = _snapshotManager.readSnapshot(new ByteArrayInputStream(out.toByteArray()));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Unable to produce a copy of the prevalent system for trying out transactions before applying them to the real system.");
		}
	}

}
