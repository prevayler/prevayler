package org.prevayler.foundation;

import org.prevayler.PrevaylerException;

/**
 * Thrown by a {@link Turn} when its pipeline has been
 * {@link Turn#abort(String,Throwable) aborted} due to an unrecoverable error
 * condition.
 */
public class TurnAbortedException extends PrevaylerException {

    private static final long serialVersionUID = 1L;

    public TurnAbortedException() {
        super();
    }

    public TurnAbortedException(String message) {
        super(message);
    }

    public TurnAbortedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TurnAbortedException(Throwable cause) {
        super(cause);
    }

}
