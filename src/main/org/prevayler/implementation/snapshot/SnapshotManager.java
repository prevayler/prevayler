package org.prevayler.implementation.snapshot;

import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.PrevalentSystemGuard;

import java.io.IOException;

public interface SnapshotManager {

    public void writeSnapshot(Object prevalentSystem, long systemVersion) throws IOException;

    public PrevalentSystemGuard recoveredPrevalentSystem();

    public Serializer primarySerializer();

}
