package org.prevayler.util;

import java.io.IOException;

import org.prevayler.Prevayler;
import org.prevayler.implementation.*;
import org.prevayler.implementation.clock.MachineClock;
import org.prevayler.implementation.log.TransactionLogger;

public class PrevaylerFactory {

	public static Prevayler createTransientPrevayler(Object prevalentSystem) {
		return new TransientPrevayler(prevalentSystem, new MachineClock());
	}

	/** Creates a SnapshotPrevayler that will use the current directory to read and write its snapshot files.
	 * @param newPrevalentSystem The newly started, "empty" prevalent system that will be used as a starting point for every system startup, until the first snapshot is taken.
	 */
	public static SnapshotPrevayler createSnapshotPrevayler(Object newPrevalentSystem) throws IOException, ClassNotFoundException {
		return createSnapshotPrevayler(newPrevalentSystem, "PrevalenceBase");
	}

	/** @param newPrevalentSystem The newly started, "empty" prevalent system that will be used as a starting point for every system startup, until the first snapshot is taken.
	 * @param prevalenceBase The directory where the snapshot files and transactionLog files will be read and written.
	 */
	public static SnapshotPrevayler createSnapshotPrevayler(Object newPrevalentSystem, String prevalenceBase) throws IOException, ClassNotFoundException {
		return new SnapshotPrevayler(newPrevalentSystem, new SnapshotManager(prevalenceBase), new TransactionLogger(prevalenceBase, new MachineClock()));
	}

}
