//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Aleksey Aristov.

package org.prevayler;

import java.io.IOException;
import java.io.Serializable;

import org.prevayler.implementation.PrevaylerImpl;
import org.prevayler.implementation.clock.MachineClock;
import org.prevayler.implementation.logging.PersistentLogger;
import org.prevayler.implementation.logging.TransactionLogger;
import org.prevayler.implementation.logging.TransientLogger;
import org.prevayler.implementation.publishing.CentralPublisher;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.publishing.censorship.LiberalTransactionCensor;
import org.prevayler.implementation.publishing.censorship.StrictTransactionCensor;
import org.prevayler.implementation.publishing.censorship.TransactionCensor;
import org.prevayler.implementation.replication.ServerListener;
import org.prevayler.implementation.replication.ClientPublisher;
import org.prevayler.implementation.snapshot.NullSnapshotManager;
import org.prevayler.implementation.snapshot.SnapshotManager;
import org.prevayler.implementation.snapshot.JavaSnapshotManager;

/** Provides easy access to all Prevayler configurations and implementations available in this distribution.
 * Static methods are also provided as short-cuts for the most common configurations. 
 * <br>By default, the Prevayler instances created by this class will write their Transactions to .transactionLog files before executing them. The FileDescriptor.sync() method is called to make sure the Java file write-buffers have been written to the operating system. Many operating systems, including most recent versions of Linux and Windows, allow the hard-drive's write-cache to be disabled. This guarantees no executed Transaction will be lost in the event of a power shortage, for example.
 * <br>Also by default, the Prevayler instances created by this class will filter out all Transactions that would throw a RuntimeException or Error if executed on the Prevalent System. This requires enough RAM to hold another copy of the prevalent system. 
 * @see Prevayler 
 */
public class PrevaylerFactory {

	private Object _prevalentSystem;
	private Clock _clock;

	private boolean _transactionFiltering = true;

	private boolean _transientMode;
	private String _prevalenceBase;
	private SnapshotManager _snapshotManager;

	private long _transactionLogSizeThreshold;
	private long _transactionLogAgeThreshold;
	
	private int _serverPort = -1;
	private String _remoteServerIpAddress;
	private int _remoteServerPort;
	public static final int DEFAULT_REPLICATION_PORT = 8756;

	private ClassLoader _classLoader;
	

	/** Creates a Prevayler that will use a directory called "PrevalenceBase" under the current directory to read and write its .snapshot and .transactionLog files.
 	 * @param newPrevalentSystem The newly started, "empty" prevalent system that will be used as a starting point for every system startup, until the first snapshot is taken.
	 */
	public static Prevayler createPrevayler(Serializable newPrevalentSystem) throws IOException, ClassNotFoundException {
		return createPrevayler(newPrevalentSystem, "PrevalenceBase");
	}


	/** Creates a Prevayler that will use the given prevalenceBase directory to read and write its .snapshot and .transactionLog files.
	 * @param newPrevalentSystem The newly started, "empty" prevalent system that will be used as a starting point for every system startup, until the first snapshot is taken.
	 * @param prevalenceBase The directory where the .snapshot files and .transactionLog files will be read and written.
	 */
	public static Prevayler createPrevayler(Serializable newPrevalentSystem, String prevalenceBase) throws IOException, ClassNotFoundException {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(newPrevalentSystem);
		factory.configurePrevalenceBase(prevalenceBase);
		return factory.create();
	}


	/** Creates a Prevayler that will execute Transactions WITHOUT writing them to disk. This is useful for running automated tests or demos MUCH faster than with a regular Prevayler.
	 * 
	 * Attempts to take snapshots on this transient Prevayler will throw an IOException.
	 * @param newPrevalentSystem The newly started, "empty" prevalent system.
	 * @see #createCheckpointPrevayler(Serializable newPrevalentSystem, String snapshotDirectory)
	 */
	public static Prevayler createTransientPrevayler(Serializable newPrevalentSystem) {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(newPrevalentSystem);
		factory.configureSnapshotManager(new NullSnapshotManager(newPrevalentSystem, "Transient Prevaylers are unable to take snapshots."));
		factory.configureTransientMode(true);
		try {
			return factory.create();
		} catch (Exception e) {
			e.printStackTrace(); //Transient Prevayler creation should not fail.
			return null;
		}
	}


	/** @deprecated Use createCheckpointPrevayler() instead of this method. Deprecated since Prevayler2.00.001.
	 */
	public static Prevayler createTransientPrevayler(Serializable newPrevalentSystem, String snapshotDirectory) {
		return createCheckpointPrevayler(newPrevalentSystem, snapshotDirectory);
	}

	/** Creates a Prevayler that will execute Transactions WITHOUT writing them to disk. Snapshots will work as "checkpoints" for the system, therefore. This is useful for stand-alone applications that have a "Save" button, for example.
	 * @param newPrevalentSystem The newly started, "empty" prevalent system that will be used as a starting point for every system startup, until the first snapshot is taken.
	 * @param snapshotDirectory The directory where the .snapshot files will be read and written.
	 */
	public static Prevayler createCheckpointPrevayler(Serializable newPrevalentSystem, String snapshotDirectory) {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(newPrevalentSystem);
		factory.configurePrevalenceBase(snapshotDirectory);
		factory.configureTransientMode(true);
		try {
			return factory.create();
		} catch (Exception e) {
			e.printStackTrace(); //Transient Prevayler creation should not fail.
			return null;
		}
	}


	private Clock clock() {
		return _clock != null ? _clock : new MachineClock();
	}


	/** Determines whether the Prevayler created by this factory should be transient (transientMode = true) or persistent (transientMode = false). A transient Prevayler will execute its Transactions WITHOUT writing them to disk. This is useful for stand-alone applications which have a "Save" button, for example, or for running automated tests MUCH faster than with a persistent Prevayler.
	 */
	public void configureTransientMode(boolean transientMode) {
		_transientMode = transientMode;		
	}


	/** Configures the Clock that will be used by the created Prevayler. The Clock interface can be implemented by the application if it requires Prevayler to use a special time source other than the machine clock (default).
	 */
	public void configureClock(Clock clock) {
		_clock = clock;
	}


	/** Configures the directory where the created Prevayler will read and write its .transactionLog and .snapshot files. The default is a directory called "PrevalenceBase" under the current directory.
	 * @param prevalenceBase Will be ignored for the .snapshot files if a SnapshotManager is configured.
	 */
	public void configurePrevalenceBase(String prevalenceBase) {
		_prevalenceBase = prevalenceBase;
	}


	/** Configures the prevalent system that will be used by the Prevayler created by this factory.
	 * @param newPrevalentSystem Will be ignored if a SnapshotManager is configured too because a SnapshotManager already has a prevalent system. If the default SnapshotManager is used, this prevalentSystem must be Serializable. If another SnapshotManager is used, this prevalentSystem must be compatible with it. 
	 * @see #configureSnapshotManager(SnapshotManager)
	 */
	public void configurePrevalentSystem(Object newPrevalentSystem) {
		_prevalentSystem = newPrevalentSystem;
	}


	/** Reserved for future implementation.
	 */
	public void configureReplicationClient(String remoteServerIpAddress, int remoteServerPort) {
		_remoteServerIpAddress = remoteServerIpAddress;
		_remoteServerPort = remoteServerPort;
	}


	/** Reserved for future implementation.
	 */
	public void configureReplicationServer(int port) {
		_serverPort = port;
	}


	/** Configures the SnapshotManager to be used by the Prevayler created by this factory. The default is a SnapshotManager which uses plain Java serialization to create its .snapshot files.
	 */
	public void configureSnapshotManager(SnapshotManager snapshotManager) {
		_snapshotManager = snapshotManager;
	}


	/** Determines whether the Prevayler created by this factory should filter out all Transactions that would throw a RuntimeException or Error if executed on the Prevalent System (default is true). This requires enough RAM to hold another copy of the prevalent system.
	 */
	public void configureTransactionFiltering(boolean transactionFiltering) {
		_transactionFiltering = transactionFiltering;
	}


	/** Returns a Prevayler created according to what was defined by calls to the configuration methods above.
	 * @throws IOException If there is trouble creating the Prevalence Base directory or reading a .transactionLog or .snapshot file.
	 * @throws ClassNotFoundException If a class of a serialized Object is not found when reading a .transactionLog or .snapshot file.
	 */
	public Prevayler create() throws IOException, ClassNotFoundException {
		SnapshotManager snapshotManager = snapshotManager();
		TransactionPublisher publisher = publisher(snapshotManager);
		if (_serverPort != -1) new ServerListener(publisher, _serverPort);
		return new PrevaylerImpl(snapshotManager, publisher);
	}


	private String prevalenceBase() {
		return _prevalenceBase != null ? _prevalenceBase : "PrevalenceBase";
	}


	private Object prevalentSystem() {
		if (_prevalentSystem == null) throw new IllegalStateException("The prevalent system must be configured.");
		return _prevalentSystem;
	}


	private TransactionPublisher publisher(SnapshotManager snapshotManager) throws IOException, ClassNotFoundException {
		if (_remoteServerIpAddress != null) return new ClientPublisher(_remoteServerIpAddress, _remoteServerPort);
		return new CentralPublisher(clock(), censor(snapshotManager), logger()); 
	}


	private TransactionCensor censor(SnapshotManager snapshotManager) {
		return _transactionFiltering
			? (TransactionCensor) new StrictTransactionCensor(snapshotManager)
			: new LiberalTransactionCensor(); 
	}


	private TransactionLogger logger() throws IOException {
		return _transientMode
			? (TransactionLogger)new TransientLogger()
			: new PersistentLogger(prevalenceBase(), _transactionLogSizeThreshold, _transactionLogAgeThreshold, classLoader());		
	}


	private SnapshotManager snapshotManager() throws ClassNotFoundException, IOException {
		return _snapshotManager != null
			? _snapshotManager
			: new JavaSnapshotManager(prevalentSystem(), prevalenceBase(), classLoader());
	}


	public void configureTransactionLogFileSizeThreshold(long sizeInBytes) {
		_transactionLogSizeThreshold = sizeInBytes;
	}

	
	public void configureTransactionLogFileAgeThreshold(long ageInMilliseconds) {
		_transactionLogAgeThreshold = ageInMilliseconds;
	}

	public void configureClassLoader(ClassLoader classLoader) {
		_classLoader = classLoader;
	}
	
	private ClassLoader classLoader() {
	 	return(_classLoader != null ? _classLoader : getClass().getClassLoader());
	}
}
