package org.prevayler;

import java.io.IOException;

import org.prevayler.implementation.CentralPublisher;
import org.prevayler.implementation.PrevaylerImpl;
import org.prevayler.implementation.SnapshotManager;
import org.prevayler.implementation.TransactionCensor;
import org.prevayler.implementation.TransactionPublisher;
import org.prevayler.implementation.clock.MachineClock;
import org.prevayler.implementation.log.PersistentLogger;
import org.prevayler.implementation.log.TransactionLogger;
import org.prevayler.implementation.log.TransientLogger;
import org.prevayler.implementation.replica.PublishingServer;

public class PrevaylerFactory {

	private Object _prevalentSystem;
	private Clock _clock;

	private String _prevalenceBase;

	private boolean _transientMode;

	private boolean _replicationServerMode;
	private String _serverIpAddress;
	private SnapshotManager _snapshotManager;


	/** Creates a Prevayler that will use a directory called "PrevalenceBase" under the current directory to read and write its snapshot and transactionLog files.
	 * @param newPrevalentSystem The newly started, "empty" prevalent system that will be used as a starting point for every system startup, until the first snapshot is taken.
	 */
	public static Prevayler createPrevayler(Object newPrevalentSystem) throws IOException, ClassNotFoundException {
		return createPrevayler(newPrevalentSystem, "PrevalenceBase");
	}


	/** @param newPrevalentSystem The newly started, "empty" prevalent system that will be used as a starting point for every system startup, until the first snapshot is taken.
	 * @param prevalenceBase The directory where the snapshot files and transactionLog files will be read and written.
	 */
	public static Prevayler createPrevayler(Object prevalentSystem, String prevalenceBase) throws IOException, ClassNotFoundException {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(prevalentSystem);
		factory.configurePrevalenceBase(prevalenceBase);
		return factory.create();
	}


	public static Prevayler createTransientPrevayler(Object newPrevalentSystem) {
		return createTransientPrevayler(newPrevalentSystem, "PrevalenceBase");
	}


	public static Prevayler createTransientPrevayler(Object prevalentSystem, String snapshotDirectory) {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(prevalentSystem);
		factory.configurePrevalenceBase(snapshotDirectory);
		factory.configureTransientMode(true);
		try {
			return factory.create();
		} catch (Exception e) {
			e.printStackTrace(); //Transient Prevayler creation should not fail.
			return null;
		}
	}


	private void configureTransientMode(boolean transientMode) {
		_transientMode = transientMode;		
	}


	private Clock clock() {
		return _clock != null ? _clock : new MachineClock();
	}


	public void configureClock(Clock clock) {
		_clock = clock;
	}


	public void configurePrevalenceBase(String prevalenceBase) {
		_prevalenceBase = prevalenceBase;
	}


	/**
	 * Configures the prevalent system that will be used by the Prevayler created by this factory.
	 * @param prevalentSystem will be ignored if a SnapshotManager is configured too because a SnapshotManager already has a prevalent system.
	 * @see configureSnapshotManager()
	 */
	public void configurePrevalentSystem(Object prevalentSystem) {
		_prevalentSystem = prevalentSystem;
	}


	public void configureReplicationClient(String serverIpAddress) {
		_serverIpAddress = serverIpAddress;		
	}


	public void configureReplicationServer(boolean serverMode) {
		_replicationServerMode = serverMode;
	}


	public void configureSnapshotManager(SnapshotManager snapshotManager) {
		_snapshotManager = snapshotManager;
	}


	public Prevayler create() throws IOException, ClassNotFoundException {
		SnapshotManager snapshotManager = snapshotManager();
		TransactionPublisher publisher = publisher(snapshotManager);
		if (_replicationServerMode) new PublishingServer(publisher);
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
		TransactionCensor censor = new TransactionCensor(snapshotManager); 
		return new CentralPublisher(clock(), censor, logger()); 
	}


	private TransactionLogger logger() throws IOException, ClassNotFoundException {
		return _transientMode
			? (TransactionLogger)new TransientLogger()
			: new PersistentLogger(prevalenceBase());		
	}


	private SnapshotManager snapshotManager() throws ClassNotFoundException, IOException {
		return _snapshotManager != null
			? _snapshotManager
			: new SnapshotManager(prevalentSystem(), prevalenceBase());
	}

}
