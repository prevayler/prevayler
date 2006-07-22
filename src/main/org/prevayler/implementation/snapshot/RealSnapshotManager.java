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

import org.prevayler.*;
import org.prevayler.foundation.*;
import org.prevayler.foundation.serialization.*;
import org.prevayler.implementation.*;

import java.io.*;
import java.util.*;

public class RealSnapshotManager<S> implements SnapshotManager<S> {

    private Map<String, ? extends Serializer<S>> _strategies;

    private String _primarySuffix;

    private PrevaylerDirectory _directory;

    private PrevalentSystemGuard<S> _recoveredPrevalentSystem;

    public RealSnapshotManager(Map<String, ? extends Serializer<S>> snapshotSerializers, String primarySnapshotSuffix, S newPrevalentSystem, PrevaylerDirectory directory, Serializer<GenericTransaction> journalSerializer) {
        for (Iterator iterator = snapshotSerializers.keySet().iterator(); iterator.hasNext();) {
            String suffix = (String) iterator.next();
            PrevaylerDirectory.checkValidSnapshotSuffix(suffix);
        }

        if (!snapshotSerializers.containsKey(primarySnapshotSuffix)) {
            throw new IllegalArgumentException("Primary suffix '" + primarySnapshotSuffix + "' does not appear in strategies map");
        }

        try {
            _strategies = snapshotSerializers;
            _primarySuffix = primarySnapshotSuffix;

            _directory = directory;
            _directory.produceDirectory();

            File latestSnapshot = _directory.latestSnapshot();
            long recoveredVersion = latestSnapshot == null ? 0 : PrevaylerDirectory.snapshotVersion(latestSnapshot);
            S recoveredPrevalentSystem = latestSnapshot == null ? DeepCopier.deepCopy(newPrevalentSystem, primarySerializer()) : readSnapshot(latestSnapshot);
            _recoveredPrevalentSystem = new PrevalentSystemGuard<S>(recoveredPrevalentSystem, recoveredVersion, journalSerializer);
        } catch (Exception e) {
            throw new SnapshotError(e);
        }
    }

    private S readSnapshot(File snapshotFile) throws Exception {
        String suffix = snapshotFile.getName().substring(snapshotFile.getName().indexOf('.') + 1);
        if (!_strategies.containsKey(suffix)) {
            throw new SnapshotError(snapshotFile.toString() + " cannot be read; only " + _strategies.keySet().toString() + " supported");
        }

        Serializer<S> serializer = _strategies.get(suffix);
        FileInputStream in = new FileInputStream(snapshotFile);
        try {
            return serializer.readObject(in);
        } finally {
            in.close();
        }
    }

    public Serializer<S> primarySerializer() {
        return _strategies.get(_primarySuffix);
    }

    public PrevalentSystemGuard<S> recoveredPrevalentSystem() {
        return _recoveredPrevalentSystem;
    }

    public void writeSnapshot(S prevalentSystem, long version) {
        try {
            File tempFile = _directory.createTempFile("snapshot" + version + "temp", "generatingSnapshot");

            OutputStream out = new FileOutputStream(tempFile);
            try {
                primarySerializer().writeObject(out, prevalentSystem);
            } finally {
                out.close();
            }

            File permanent = snapshotFile(version);
            permanent.delete();
            if (!tempFile.renameTo(permanent)) {
                throw new SnapshotError("Temporary snapshot file generated: " + tempFile + "\nUnable to rename it permanently to: " + permanent);
            }
        } catch (Exception e) {
            throw new SnapshotError(e);
        }
    }

    private File snapshotFile(long version) {
        return _directory.snapshotFile(version, _primarySuffix);
    }

}
