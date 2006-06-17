package org.prevayler.implementation.snapshot;

import org.prevayler.PrevalenceError;

public class SnapshotError extends PrevalenceError {

    private static final long serialVersionUID = 1L;

    public SnapshotError() {
        super();
    }

    public SnapshotError(String message) {
        super(message);
    }

    public SnapshotError(String message, Throwable cause) {
        super(message, cause);
    }

    public SnapshotError(Throwable cause) {
        super(cause);
    }

}
