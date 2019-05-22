package org.prevayler.implementation.snapshot;

import org.prevayler.foundation.serialization.JavaSerializer;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.PrevalentSystemGuard;
import org.prevayler.implementation.PrevaylerDirectory;

import java.io.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class GenericSnapshotManager<P> {

  private Map<String, ? extends Serializer> _strategies;
  private String _primarySuffix;
  private PrevaylerDirectory _directory;
  private PrevalentSystemGuard<P> _recoveredPrevalentSystem;

  public GenericSnapshotManager(Map<String, ? extends Serializer> snapshotSerializers, String primarySnapshotSuffix, P newPrevalentSystem, PrevaylerDirectory directory, Serializer journalSerializer)
      throws Exception {
    for (Iterator<String> iterator = snapshotSerializers.keySet().iterator(); iterator.hasNext(); ) {
      String suffix = iterator.next();
      PrevaylerDirectory.checkValidSnapshotSuffix(suffix);
    }

    if (!snapshotSerializers.containsKey(primarySnapshotSuffix)) {
      throw new IllegalArgumentException("Primary suffix '" + primarySnapshotSuffix + "' does not appear in strategies map");
    }

    _strategies = snapshotSerializers;
    _primarySuffix = primarySnapshotSuffix;

    _directory = directory;
    _directory.produceDirectory();

    File latestSnapshot = _directory.latestSnapshot();
    long recoveredVersion = latestSnapshot == null ? 0 : PrevaylerDirectory.snapshotVersion(latestSnapshot);
    P recoveredPrevalentSystem = latestSnapshot == null
        ? newPrevalentSystem
        : readSnapshot(latestSnapshot);
    _recoveredPrevalentSystem = new PrevalentSystemGuard<P>(recoveredPrevalentSystem, recoveredVersion, journalSerializer);
  }

  GenericSnapshotManager(P newPrevalentSystem) {
    _strategies = Collections.singletonMap("snapshot", new JavaSerializer());
    _primarySuffix = "snapshot";
    _directory = null;
    _recoveredPrevalentSystem = new PrevalentSystemGuard<P>(newPrevalentSystem, 0, new JavaSerializer());
  }


  public Serializer primarySerializer() {
    return _strategies.get(_primarySuffix);
  }

  public PrevalentSystemGuard<P> recoveredPrevalentSystem() {
    return _recoveredPrevalentSystem;
  }

  public File writeSnapshot(P prevalentSystem, long version) throws Exception {
    File tempFile = _directory.createTempFile("snapshot" + version + "temp", "generatingSnapshot");

    writeSnapshot(prevalentSystem, tempFile);

    File permanent = snapshotFile(version);
    permanent.delete();
    if (!tempFile.renameTo(permanent)) throw new IOException(
        "Temporary snapshot file generated: " + tempFile + "\nUnable to rename it permanently to: " + permanent);

    return permanent;
  }

  private void writeSnapshot(P prevalentSystem, File snapshotFile) throws Exception {
    OutputStream out = new FileOutputStream(snapshotFile);
    try {
      primarySerializer().writeObject(out, prevalentSystem);
    } finally {
      out.close();
    }
  }


  private File snapshotFile(long version) {
    return _directory.snapshotFile(version, _primarySuffix);
  }

  @SuppressWarnings("unchecked")
  private P readSnapshot(File snapshotFile) throws Exception {
    String suffix = snapshotFile.getName().substring(snapshotFile.getName().indexOf('.') + 1);
    if (!_strategies.containsKey(suffix)) throw new IOException(
        snapshotFile.toString() + " cannot be read; only " + _strategies.keySet().toString() + " supported");

    Serializer serializer = _strategies.get(suffix);
    FileInputStream in = new FileInputStream(snapshotFile);
    try {
      return (P) serializer.readObject(in);
    } finally {
      in.close();
    }
  }

}
