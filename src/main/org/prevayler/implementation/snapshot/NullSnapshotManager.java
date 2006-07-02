// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation.snapshot;

import org.prevayler.GenericTransaction;
import org.prevayler.foundation.serialization.JavaSerializer;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.PrevalentSystemGuard;

public class NullSnapshotManager<S> implements SnapshotManager<S> {

    private final String _snapshotAttemptErrorMessage;

    private final PrevalentSystemGuard<S> _recoveredPrevalentSystem;

    private final JavaSerializer<S> _primarySerializer;

    public NullSnapshotManager(S newPrevalentSystem, String snapshotAttemptErrorMessage) {
        _snapshotAttemptErrorMessage = snapshotAttemptErrorMessage;
        _primarySerializer = new JavaSerializer<S>();
        _recoveredPrevalentSystem = new PrevalentSystemGuard<S>(newPrevalentSystem, 0, new JavaSerializer<GenericTransaction>());
    }

    public void writeSnapshot(@SuppressWarnings("unused") S prevalentSystem, @SuppressWarnings("unused") long version) {
        throw new SnapshotError(_snapshotAttemptErrorMessage);
    }

    public PrevalentSystemGuard<S> recoveredPrevalentSystem() {
        return _recoveredPrevalentSystem;
    }

    public Serializer<S> primarySerializer() {
        return _primarySerializer;
    }

}
