package org.prevayler;

import java.io.IOException;

import org.prevayler.implementation.PrevaylerImpl;
import org.prevayler.implementation.SnapshotManager;
import org.prevayler.implementation.TransientPublisher;
import org.prevayler.implementation.clock.MachineClock;
import org.prevayler.implementation.log.TransactionLogger;

public class PrevaylerFactory {

	private Object _prevalentSystem;
	private String _prevalenceBase;

	public void configurePrevalentSystem(Object prevalentSystem) {
		_prevalentSystem = prevalentSystem;
	}

	public void configurePrevalenceBase(String prevalenceBase) {
		_prevalenceBase = prevalenceBase;
	}

	public Prevayler create() throws IOException, ClassNotFoundException {
		return createPrevayler(_prevalentSystem, _prevalenceBase);
	}

	public static Prevayler createTransientPrevayler(Object newPrevalentSystem) {
		return createTransientPrevayler(newPrevalentSystem, "PrevalenceBase");
	}

	public static Prevayler createTransientPrevayler(Object newPrevalentSystem, String snapshotDirectory) {
		try {
			return new PrevaylerImpl(newPrevalentSystem, new SnapshotManager(snapshotDirectory), new TransientPublisher(new MachineClock()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/** Creates a PrevaylerImpl that will use the current directory to read and write its snapshot files.
	 * @param newPrevalentSystem The newly started, "empty" prevalent system that will be used as a starting point for every system startup, until the first snapshot is taken.
	 */
	public static Prevayler createPrevayler(Object newPrevalentSystem) throws IOException, ClassNotFoundException {
		return createPrevayler(newPrevalentSystem, "PrevalenceBase");
	}

	/** @param newPrevalentSystem The newly started, "empty" prevalent system that will be used as a starting point for every system startup, until the first snapshot is taken.
	 * @param prevalenceBase The directory where the snapshot files and transactionLog files will be read and written.
	 */
	public static Prevayler createPrevayler(Object newPrevalentSystem, String prevalenceBase) throws IOException, ClassNotFoundException {
		return new PrevaylerImpl(newPrevalentSystem, new SnapshotManager(prevalenceBase), new TransactionLogger(prevalenceBase, new MachineClock()));
	}

}
