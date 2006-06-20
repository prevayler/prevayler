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

import org.prevayler.foundation.serialization.JavaSerializer;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.PrevalentSystemGuard;

public class NullSnapshotManager<T> implements SnapshotManager<T> {

    private final String _snapshotAttemptErrorMessage;

    private final PrevalentSystemGuard<T> _recoveredPrevalentSystem;

    private final JavaSerializer<T> _primarySerializer;

    public NullSnapshotManager(T newPrevalentSystem, String snapshotAttemptErrorMessage) {
        _snapshotAttemptErrorMessage = snapshotAttemptErrorMessage;
        _primarySerializer = new JavaSerializer<T>();
        _recoveredPrevalentSystem = new PrevalentSystemGuard<T>(newPrevalentSystem, 0, new JavaSerializer<Object>());
    }

    public void writeSnapshot(@SuppressWarnings("unused") T prevalentSystem, @SuppressWarnings("unused") long version) {
        throw new SnapshotError(_snapshotAttemptErrorMessage);
    }

    public PrevalentSystemGuard<T> recoveredPrevalentSystem() {
        return _recoveredPrevalentSystem;
    }

    public Serializer<T> primarySerializer() {
        return _primarySerializer;
    }

}
