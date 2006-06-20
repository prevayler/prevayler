// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation.publishing.censorship;

import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.PrevalentSystemGuard;
import org.prevayler.implementation.TransactionTimestamp;
import org.prevayler.implementation.snapshot.SnapshotManager;

public class StrictTransactionCensor<T> implements TransactionCensor<T> {

    private final PrevalentSystemGuard<T> _king;

    private PrevalentSystemGuard<T> _royalFoodTaster;

    private final Serializer<T> _snapshotSerializer;

    public StrictTransactionCensor(SnapshotManager<T> snapshotManager) {
        _king = snapshotManager.recoveredPrevalentSystem();
        // The _royalFoodTaster cannot be initialized here, or else the pending
        // transactions will not be applied to it.
        _snapshotSerializer = snapshotManager.primarySerializer();
    }

    public <X> void approve(TransactionTimestamp<X, T> transactionTimestamp) {
        try {
            TransactionTimestamp<X, T> timestampCopy = transactionTimestamp.cleanCopy();
            PrevalentSystemGuard<T> royalFoodTaster = royalFoodTaster(transactionTimestamp.systemVersion() - 1);
            royalFoodTaster.receive(timestampCopy);
        } catch (RuntimeException rx) {
            letTheFoodTasterDie();
            throw rx;
        } catch (Error error) {
            letTheFoodTasterDie();
            throw error;
        }
    }

    private void letTheFoodTasterDie() {
        // At this moment there might be
        // transactions that have already been
        // approved by this censor but have not yet
        // been applied to the _king. It is a
        // requirement, therefore, that the
        // _royalFoodTaster must not be initialized
        // now, but only when the next transaction
        // arrives to be approved.
        _royalFoodTaster = null;
    }

    private PrevalentSystemGuard<T> royalFoodTaster(long systemVersion) {
        if (_royalFoodTaster == null) {
            produceNewFoodTaster(systemVersion);
        }
        return _royalFoodTaster;
    }

    private void produceNewFoodTaster(long systemVersion) {
        try {
            _royalFoodTaster = _king.deepCopy(systemVersion, _snapshotSerializer);
        } catch (Exception ex) {
            throw new FoodTasterError("Unable to produce a copy of the prevalent system for trying out transactions before applying them to the real system.", ex);
        }
    }

}
