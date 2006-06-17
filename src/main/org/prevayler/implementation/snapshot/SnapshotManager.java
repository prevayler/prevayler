package org.prevayler.implementation.snapshot;

import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.PrevalentSystemGuard;

public interface SnapshotManager {

    public void writeSnapshot(Object prevalentSystem, long systemVersion);

    public PrevalentSystemGuard recoveredPrevalentSystem();

    public Serializer primarySerializer();

}
