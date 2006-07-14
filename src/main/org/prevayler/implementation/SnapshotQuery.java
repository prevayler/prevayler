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

import static org.prevayler.Safety.Journaling.TRANSIENT;
import static org.prevayler.Safety.Locking.SHARED;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.Safety;
import org.prevayler.implementation.snapshot.SnapshotManager;

@Safety(journaling = TRANSIENT, locking = SHARED) public class SnapshotQuery<S> implements GenericTransaction<S, Void, RuntimeException> {

    private final SnapshotManager<S> _snapshotManager;

    public SnapshotQuery(SnapshotManager<S> snapshotManager) {
        _snapshotManager = snapshotManager;
    }

    public Void executeOn(PrevalenceContext<? extends S> prevalenceContext) {
        _snapshotManager.writeSnapshot(prevalenceContext.prevalentSystem(), prevalenceContext.systemVersion());
        return null;
    }

}
