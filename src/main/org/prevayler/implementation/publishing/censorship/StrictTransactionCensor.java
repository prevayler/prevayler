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

public class StrictTransactionCensor<S> implements TransactionCensor<S> {

    private final PrevalentSystemGuard<S> _king;

    private PrevalentSystemGuard<S> _royalFoodTaster;

    private final Serializer<S> _snapshotSerializer;

    public StrictTransactionCensor(SnapshotManager<S> snapshotManager) {
        _king = snapshotManager.recoveredPrevalentSystem();
        // The _royalFoodTaster cannot be initialized here, or else the pending
        // transactions will not be applied to it.
        _snapshotSerializer = snapshotManager.primarySerializer();
    }

    public <R, E extends Exception> boolean approve(TransactionTimestamp<S, R, E> transactionTimestamp) {
        boolean approved = false;
        try {
            PrevalentSystemGuard<S> royalFoodTaster = royalFoodTaster(transactionTimestamp.systemVersion() - 1);
            royalFoodTaster.receive(transactionTimestamp);
            approved = !transactionTimestamp.capsule().threwRuntimeException();
        } finally {
            if (approved) {
                transactionTimestamp.capsule().cleanUp();
            } else {
                letTheFoodTasterDie();
            }
        }
        return approved;
    }

    private void letTheFoodTasterDie() {
        // At this moment there might be transactions that have already been
        // approved by this censor but have not yet been applied to the _king.
        // It is a requirement, therefore, that the _royalFoodTaster must not be
        // initialized now, but only when the next transaction arrives to be
        // approved.
        _royalFoodTaster = null;
    }

    private PrevalentSystemGuard<S> royalFoodTaster(long systemVersion) {
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
