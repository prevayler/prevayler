//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Jacob Kjome.

package org.prevayler.implementation.snapshot;

import java.io.File;
import java.io.IOException;

public class NullSnapshotManager<P> extends GenericSnapshotManager<P> {

  private final String _snapshotAttemptErrorMessage;

  public NullSnapshotManager(P newPrevalentSystem, String snapshotAttemptErrorMessage) {
    super(newPrevalentSystem);
    _snapshotAttemptErrorMessage = snapshotAttemptErrorMessage;
  }

  public File writeSnapshot(P prevalentSystem, long version) throws IOException {
    throw new IOException(_snapshotAttemptErrorMessage);
  }

}
