//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Jon Tirsèn.

package org.prevayler.implementation.publishing.censorship;

import org.prevayler.Transaction;
import org.prevayler.foundation.DeepCopier;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.snapshot.GenericSnapshotManager;

import java.util.Date;

public class StrictTransactionCensor implements TransactionCensor {

	private final Object _king;
	private Object _royalFoodTaster;
	private final GenericSnapshotManager _snapshotManager;

	private final Serializer _journalSerializer;


	public StrictTransactionCensor(GenericSnapshotManager snapshotManager, Serializer journalSerializer) {
		_snapshotManager = snapshotManager;
		_journalSerializer = journalSerializer;
		_king = _snapshotManager.recoveredPrevalentSystem();
		//The _royalFoodTaster cannot be initialized here, or else the pending transactions will not be applied to it.
	}

	public void approve(Transaction transaction, Date executionTime) throws RuntimeException, Error {
		try {
			Transaction transactionCopy = deepCopy(transaction);
			transactionCopy.executeOn(royalFoodTaster(), executionTime);
		} catch (RuntimeException rx) {
			letTheFoodTasterDie();
			throw rx;
		} catch (Error error) {
			letTheFoodTasterDie();
			throw error;
		}
	}

	private Transaction deepCopy(Transaction transaction) {
		try {
			return (Transaction)DeepCopier.deepCopy(transaction, _journalSerializer);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Unable to produce a copy of the transaction for trying out before applying it to the real system.");
		}
	}

	private void letTheFoodTasterDie() {
		_royalFoodTaster = null;  // At this moment there might be transactions that have already been approved by this censor but have not yet been applied to the _king. It is a requirement, therefore, that the _royalFoodTaster must not be initialized now, but only when the next transaction arrives to be approved.
	}

	private Object royalFoodTaster() {
		if (_royalFoodTaster == null) produceNewFoodTaster();
		return _royalFoodTaster;
	}

	private void produceNewFoodTaster() {
		try {
			synchronized (_king) {
				_royalFoodTaster = DeepCopier.deepCopy(_king, _snapshotManager.primarySerializer());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Unable to produce a copy of the prevalent system for trying out transactions before applying them to the real system.");
		}
	}

}


