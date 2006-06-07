//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Jacob Kjome.

package org.prevayler.implementation.snapshot;

import org.prevayler.foundation.serialization.JavaSerializer;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.PrevalentSystemGuard;

import java.io.IOException;

public class NullSnapshotManager implements SnapshotManager {

    private final String _snapshotAttemptErrorMessage;

    private final PrevalentSystemGuard _recoveredPrevalentSystem;

    private final JavaSerializer _primarySerializer;

    public NullSnapshotManager(Object newPrevalentSystem, String snapshotAttemptErrorMessage) {
        _snapshotAttemptErrorMessage = snapshotAttemptErrorMessage;
        _primarySerializer = new JavaSerializer();
        _recoveredPrevalentSystem = new PrevalentSystemGuard(newPrevalentSystem, 0, _primarySerializer);
    }

    public void writeSnapshot(Object prevalentSystem, long version) throws IOException {
        throw new SnapshotException(_snapshotAttemptErrorMessage);
    }

    public PrevalentSystemGuard recoveredPrevalentSystem() {
        return _recoveredPrevalentSystem;
    }

    public Serializer primarySerializer() {
        return _primarySerializer;
    }

}
