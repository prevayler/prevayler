//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Jacob Kjome.

package org.prevayler.implementation.snapshot;

import java.io.IOException;

public class NullSnapshotManager extends GenericSnapshotManager {

	private final String _snapshotAttemptErrorMessage;

	public NullSnapshotManager(Object newPrevalentSystem, String snapshotAttemptErrorMessage) {
		super(newPrevalentSystem);
		_snapshotAttemptErrorMessage = snapshotAttemptErrorMessage;
	}

	public void writeSnapshot(Object prevalentSystem, long version) throws IOException {
		throw new IOException(_snapshotAttemptErrorMessage);
	}

}
