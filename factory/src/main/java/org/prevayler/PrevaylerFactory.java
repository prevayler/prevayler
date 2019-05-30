//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Aleksey Aristov, Carlos Villela, Justin Sampson.

package org.prevayler;

import org.prevayler.foundation.monitor.Monitor;
import org.prevayler.foundation.monitor.SimpleMonitor;
import org.prevayler.foundation.network.OldNetworkImpl;
import org.prevayler.foundation.serialization.JavaSerializer;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.foundation.serialization.XStreamSerializer;
import org.prevayler.implementation.PrevaylerDirectory;
import org.prevayler.implementation.PrevaylerImpl;
import org.prevayler.implementation.clock.MachineClock;
import org.prevayler.implementation.journal.Journal;
import org.prevayler.implementation.journal.PersistentJournal;
import org.prevayler.implementation.journal.TransientJournal;
import org.prevayler.implementation.publishing.CentralPublisher;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.replication.ClientPublisher;
import org.prevayler.implementation.replication.ServerListener;
import org.prevayler.implementation.snapshot.GenericSnapshotManager;
import org.prevayler.implementation.snapshot.NullSnapshotManager;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides easy access to all Prevayler configurations and implementations
 * available in this distribution. Static methods are also provided as
 * short-cuts for the most common configurations. <br>
 * <br>
 * By default, the Prevayler instances created by this class will write their
 * Transactions to .journal files before executing them. The
 * FileDescriptor.sync() method is called to make sure the Java file
 * write-buffers have been written to the operating system. Many operating
 * systems, including most recent versions of Linux and Windows, allow the
 * hard-drive's write-cache to be disabled. This guarantees no executed
 * Transaction will be lost in the event of a power shortage, for example. <br>
 * <br>
 * Also by default, the Prevayler instances created by this class will execute
 * deep copies of transactions, not the transactions themselves, so that
 * unrecoverable changes to the prevalent system and unrecoverable uses of
 * reference equality inside transactions fail fast as they would upon recovery.
 *
 * @param <P> The type of object you intend to persist as a Prevalent System.
 *        <br>
 * @see Prevayler
 */
public class PrevaylerFactory<P> {

  private P _prevalentSystem;
  private Clock _clock;

  private boolean _transactionDeepCopyMode = true;

  private boolean _transientMode;
  private String _prevalenceDirectory;
  private NullSnapshotManager<P> _nullSnapshotManager;

  private long _journalSizeThreshold;
  private long _journalAgeThreshold;
  private boolean _journalDiskSync = true;

  private int _serverPort = -1;
  private String _remoteServerIpAddress;
  private int _remoteServerPort;
  public static final int DEFAULT_REPLICATION_PORT = 8756;

  private Monitor _monitor;

  private Serializer _journalSerializer;
  private String _journalSuffix;
  private Map<String, Serializer> _snapshotSerializers = new HashMap<String, Serializer>();
  private String _primarySnapshotSuffix;

  /**
   * <i>Example:</i> <br>
   * <code>
   * <br>PrevaylerFactory&lt;MyObjectToPersist&gt; f = new PrevaylerFactory&lt;MyObjectToPersist&gt;();
   * <br></code> <br>
   * Use if you want access to any configuration options not available via the
   * static method short-cuts.
   */
  public PrevaylerFactory() {
  }

  /**
   * Creates a Prevayler that will use the given prevalenceBase directory to read
   * and write its .snapshot and .journal files, using standard Java
   * serialization. This requires that the Prevalent System and all Transaction
   * implementations used by the Prevayler are Java-Serializable. <br>
   * <br>
   * <i>Example:</i> <br>
   * <code>
   * <br><i>//Your object:</i>
   * <br>MyObjectToPersist newPrevalentSystem = new MyObjectToPersist();
   * <br>String prevalenceBase = "myDirectory";
   * <br><b>Prevayler&lt;MyObjectToPersist&gt; prevayler = PrevaylerFactory.createPrevayler(newPrevalentSystem, prevalenceBase);</b>
   * <br></code>
   *
   * @param newPrevalentSystem The newly started, "empty" prevalent system that
   *                           will be used as a starting point for every system
   *                           startup, until the first snapshot is taken.
   * @param prevalenceBase     The directory where the .snapshot files and
   *                           .journal files will be read and written.
   */
  public static <P> Prevayler<P> createPrevayler(P newPrevalentSystem, String prevalenceBase) throws Exception {
    PrevaylerFactory<P> factory = new PrevaylerFactory<P>();
    factory.configurePrevalentSystem(newPrevalentSystem);
    factory.configurePrevalenceDirectory(prevalenceBase);
    return factory.create();
  }

  /**
   * Creates a Prevayler that will use a directory called "PrevalenceBase" under
   * the current directory to read and write its .snapshot and .journal files,
   * using standard Java serialization. This requires that the Prevalent System
   * and all Transaction implementations used by the Prevayler are
   * Java-Serializable.
   *
   * @param newPrevalentSystem The newly started, "empty" prevalent system that
   *                           will be used as a starting point for every system
   *                           startup, until the first snapshot is taken.
   * @see #createPrevayler(Object, String)
   */
  public static <P> Prevayler<P> createPrevayler(P newPrevalentSystem) throws Exception {
    return createPrevayler(newPrevalentSystem, "PrevalenceBase");
  }

  /**
   * Creates a Prevayler that will execute Transactions WITHOUT writing them to
   * disk. Snapshots will work as "checkpoints" for the system, therefore. This is
   * useful for stand-alone applications that have a "Save" button, for example.
   * The Prevayler will use standard Java serialization for reading and writing
   * its .snapshot files, which requires that the Prevalent System is
   * Java-Serializable.
   *
   * @param newPrevalentSystem The newly started, "empty" prevalent system that
   *                           will be used as a starting point for every system
   *                           startup, until the first snapshot is taken.
   * @param snapshotDirectory  The directory where the .snapshot files will be
   *                           read and written.
   * @see #createPrevayler(Object, String)
   */
  public static <P> Prevayler<P> createCheckpointPrevayler(P newPrevalentSystem, String snapshotDirectory) {
    PrevaylerFactory<P> factory = new PrevaylerFactory<P>();
    factory.configurePrevalentSystem(newPrevalentSystem);
    factory.configurePrevalenceDirectory(snapshotDirectory);
    factory.configureTransientMode(true);
    try {
      return factory.create();
    } catch (Exception e) {
      e.printStackTrace(); // Transient Prevayler creation should not fail.
      return null;
    }
  }

  /**
   * Creates a Prevayler that will execute Transactions WITHOUT writing them to
   * disk. This is useful for running automated tests or demos MUCH faster than
   * with a regular Prevayler.
   * <p/>
   * Attempts to take snapshots on this transient Prevayler will throw an
   * IOException.
   *
   * @param newPrevalentSystem The newly started, "empty" prevalent system.
   * @see #createCheckpointPrevayler(Object, String)
   */
  public static <P> Prevayler<P> createTransientPrevayler(P newPrevalentSystem) {
    PrevaylerFactory<P> factory = new PrevaylerFactory<P>();
    factory.configurePrevalentSystem(newPrevalentSystem);
    factory.configureNullSnapshotManager(
        new NullSnapshotManager<P>(newPrevalentSystem, "Transient Prevaylers are unable to take snapshots."));
    factory.configureTransientMode(true);
    try {
      return factory.create();
    } catch (Exception e) {
      e.printStackTrace(); // Transient Prevayler creation should not fail.
      return null;
    }
  }

  /**
   * @deprecated Use createCheckpointPrevayler() instead of this method.
   *             Deprecated since Prevayler2.00.001.
   */
  @Deprecated
  public static <P> Prevayler<P> createTransientPrevayler(P newPrevalentSystem, String snapshotDirectory) {
    return createCheckpointPrevayler(newPrevalentSystem, snapshotDirectory);
  }

  private Clock clock() {
    return _clock != null ? _clock : new MachineClock();
  }

  /**
   * Configures the prevalent system that will be used by the Prevayler created by
   * this factory.
   *
   * @param newPrevalentSystem If the default Serializer is used, this
   *                           prevalentSystem must be Serializable. If another
   *                           Serializer is used, this prevalentSystem must be
   *                           compatible with it.
   * @see #configureSnapshotSerializer(String, Serializer)
   */
  public void configurePrevalentSystem(P newPrevalentSystem) {
    _prevalentSystem = newPrevalentSystem;
  }

  /**
   * Configures the directory where the created Prevayler will read and write its
   * .journal and .snapshot files. The default is a directory called
   * "PrevalenceBase" under the current directory.
   *
   * @param prevalenceDirectory Will be ignored for the .snapshot files if a
   *                            SnapshotManager is configured.
   */
  public void configurePrevalenceDirectory(String prevalenceDirectory) {
    _prevalenceDirectory = prevalenceDirectory;
  }

  /**
   * Configures whether deep copies of transactions are executed instead of the
   * transactions themselves, upon calling ".execute" on the created Prevayler.
   * The default is <code>true</code>.
   *
   * @param transactionDeepCopyMode <br>
   *                                <br>
   *                                If <code>false</code>, references passed in to
   *                                transactions are used naturally, as they are
   *                                during ordinary Java method calls, allowing
   *                                their underlying objects to be changed inside
   *                                transactions. However, any unrecoverable
   *                                changes to the prevalent system and
   *                                unrecoverable uses of reference equality
   *                                inside transactions will not fail fast as they
   *                                would upon recovery. <br>
   *                                <br>
   *                                If <code>true</code> (default), a deep copy of
   *                                the transaction is executed each time. This
   *                                allows any unrecoverable changes to the
   *                                prevalent system and unrecoverable uses of
   *                                reference equality inside transactions to fail
   *                                fast as they would upon recovery. However, it
   *                                only allows changes to deep copies of the
   *                                objects passed in, not the original objects.
   */
  public void configureTransactionDeepCopy(boolean transactionDeepCopyMode) {
    _transactionDeepCopyMode = transactionDeepCopyMode;
  }

  /**
   * Configures the Clock that will be used by the created Prevayler. The Clock
   * interface can be implemented by the application if it requires Prevayler to
   * use a special time source other than the machine clock (default).
   */
  public void configureClock(Clock clock) {
    _clock = clock;
  }

  /**
   * Assigns a monitor object to receive notifications from Prevayler. This is
   * useful for logging or sending eMails to system administrators, for example.
   * If this method is not called or if null is passed as a parameter, a
   * SimpleMonitor will be used to log notification on System.err.
   *
   * @param monitor the Monitor implementation to use.
   * @see org.prevayler.foundation.monitor.SimpleMonitor
   */
  public void configureMonitor(Monitor monitor) {
    _monitor = monitor;
  }

  /**
   * Determines whether the Prevayler created by this factory should be transient
   * or persistent. The default is <code>false</code> (persistent).
   *
   * @param transientMode <br>
   *                      <br>
   *                      If <code>true</code>, a "transient" Prevayler will be
   *                      created, which will execute its Transactions WITHOUT
   *                      writing them to disk. This is useful for stand-alone
   *                      applications which have a "Save" button, for example, or
   *                      for running automated tests MUCH faster than with a
   *                      persistent Prevayler. <br>
   *                      <br>
   *                      If <code>false</code> (default), a persistent Prevayler
   *                      will be created.
   */
  public void configureTransientMode(boolean transientMode) {
    _transientMode = transientMode;
  }

  /**
   * Reserved for future implementation.
   */
  public void configureReplicationClient(String remoteServerIpAddress, int remoteServerPort) {
    _remoteServerIpAddress = remoteServerIpAddress;
    _remoteServerPort = remoteServerPort;
  }

  /**
   * Reserved for future implementation.
   */
  public void configureReplicationServer(int port) {
    _serverPort = port;
  }

  private void configureNullSnapshotManager(NullSnapshotManager<P> snapshotManager) {
    _nullSnapshotManager = snapshotManager;
  }

  /**
   * Configures the size (in bytes) of the journal file. When the current journal
   * exceeds this size, a new journal is created.
   */
  public void configureJournalFileSizeThreshold(long sizeInBytes) {
    _journalSizeThreshold = sizeInBytes;
  }

  /**
   * Sets the age (in milliseconds) of the journal file. When the current journal
   * expires, a new journal is created.
   */
  public void configureJournalFileAgeThreshold(long ageInMilliseconds) {
    _journalAgeThreshold = ageInMilliseconds;
  }

  /**
   * Configures whether the journal will sync writes to disk. The default is
   * <code>true</code>.
   *
   * @param journalDiskSync <br>
   *                        <br>
   *                        If <code>false</code>, transactions may execute
   *                        without necessarily being written to the physical
   *                        disk. Transactions are still flushed to the operating
   *                        system before being executed, but
   *                        FileDescriptor.sync() is never called. This increases
   *                        transaction throughput dramatically, but allows
   *                        transactions to be lost if the system does not shut
   *                        down cleanly. Calling {@link Prevayler#close()} will
   *                        close the underlying journal file and therefore cause
   *                        all transactions to be written to disk. <br>
   *                        <br>
   *                        If <code>true</code> (default), every transaction is
   *                        forced to be written to the physical disk before it is
   *                        executed (using
   *                        {@link java.io.FileDescriptor#sync()}). (Many
   *                        transactions may be written at once, but no
   *                        transaction will be executed before it is written to
   *                        disk.)
   */
  public void configureJournalDiskSync(boolean journalDiskSync) {
    _journalDiskSync = journalDiskSync;
  }
  
  public void configureJournalSerializer(JavaSerializer serializer) {
    configureJournalSerializer("journal", serializer);
  }

  public void configureJournalSerializer(XStreamSerializer serializer) {
    configureJournalSerializer("xstreamjournal", serializer);
  }

  /**
   * Configures the transaction journal Serializer to be used by the Prevayler
   * created by this factory. Only one Serializer is supported at a time. If you
   * want to change the Serializer of a system in production, you will have to
   * take a snapshot first because the journal files written by the previous
   * Serializer will not be read.
   */
  public void configureJournalSerializer(String suffix, Serializer serializer) {
    PrevaylerDirectory.checkValidJournalSuffix(suffix);

    if (_journalSerializer != null) {
      throw new IllegalStateException("Read the javadoc to this method.");
    }

    _journalSerializer = serializer;
    _journalSuffix = suffix;
  }

  public void configureSnapshotSerializer(JavaSerializer serializer) {
    configureSnapshotSerializer("snapshot", serializer);
  }

  public void configureSnapshotSerializer(XStreamSerializer serializer) {
    configureSnapshotSerializer("xstreamsnapshot", serializer);
  }

  /**
   * Configure a serialization strategy for snapshots. This may be called any
   * number of times with different suffixes to configure different strategies for
   * reading existing snapshots. The first call to this method establishes the
   * <i>primary</i> strategy, which will be used for writing snapshots as well as
   * for deep-copying the prevalent system whenever necessary.
   */
  public void configureSnapshotSerializer(String suffix, Serializer serializer) {
    PrevaylerDirectory.checkValidSnapshotSuffix(suffix);
    _snapshotSerializers.put(suffix, serializer);
    if (_primarySnapshotSuffix == null) {
      _primarySnapshotSuffix = suffix;
    }
  }

  /**
   * Returns a Prevayler created according to what was defined by calls to the
   * configuration methods above.
   *
   * @throws IOException            If there is trouble creating the Prevalence
   *                                Base directory or reading a .journal or
   *                                .snapshot file.
   * @throws ClassNotFoundException If a class of a serialized Object is not found
   *                                when reading a .journal or .snapshot file.
   */
  public Prevayler<P> create() throws Exception {
    GenericSnapshotManager<P> snapshotManager = snapshotManager();
    TransactionPublisher<P> publisher = publisher(snapshotManager);
    if (_serverPort != -1)
      new ServerListener<P>(publisher, new OldNetworkImpl(), _serverPort);
    return new PrevaylerImpl<P>(snapshotManager, publisher, journalSerializer(), _transactionDeepCopyMode);
  }

  private String prevalenceDirectory() {
    return _prevalenceDirectory != null ? _prevalenceDirectory : "Prevalence";
  }

  private P prevalentSystem() {
    if (_prevalentSystem == null)
      throw new IllegalStateException("The prevalent system must be configured.");
    return _prevalentSystem;
  }

  private TransactionPublisher<P> publisher(GenericSnapshotManager<P> snapshotManager) throws IOException {
    if (_remoteServerIpAddress != null)
      return new ClientPublisher<P>(new OldNetworkImpl(), _remoteServerIpAddress, _remoteServerPort);
    return new CentralPublisher<P>(clock(), journal());
  }

  private Journal<P> journal() throws IOException {
    if (_transientMode) {
      return new TransientJournal<P>();
    } else {
      PrevaylerDirectory directory = new PrevaylerDirectory(prevalenceDirectory());
      return new PersistentJournal<P>(directory, _journalSizeThreshold, _journalAgeThreshold, _journalDiskSync,
          journalSuffix(), monitor());
    }
  }

  private Serializer journalSerializer() {
    if (_journalSerializer != null)
      return _journalSerializer;
    return new JavaSerializer();
  }

  private String journalSuffix() {
    return _journalSuffix != null ? _journalSuffix : "journal";
  }

  private GenericSnapshotManager<P> snapshotManager() throws Exception {
    if (_nullSnapshotManager != null)
      return _nullSnapshotManager;

    PrevaylerDirectory directory = new PrevaylerDirectory(prevalenceDirectory());
    if (!_snapshotSerializers.isEmpty())
      return new GenericSnapshotManager<P>(_snapshotSerializers, _primarySnapshotSuffix, prevalentSystem(), directory,
          journalSerializer());

    String snapshotSuffix = "snapshot";
    JavaSerializer snapshotSerializer = new JavaSerializer();
    return new GenericSnapshotManager<P>(Collections.singletonMap(snapshotSuffix, snapshotSerializer), snapshotSuffix,
        prevalentSystem(), directory, journalSerializer());
  }

  private Monitor monitor() {
    return _monitor != null ? _monitor : new SimpleMonitor(System.err);
  }
}
