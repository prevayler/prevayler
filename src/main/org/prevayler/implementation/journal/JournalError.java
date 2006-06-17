package org.prevayler.implementation.journal;

import org.prevayler.PrevalenceError;

public class JournalError extends PrevalenceError {

    private static final long serialVersionUID = 1L;

    public JournalError() {
        super();
    }

    public JournalError(String message) {
        super(message);
    }

    public JournalError(String message, Throwable cause) {
        super(message, cause);
    }

    public JournalError(Throwable cause) {
        super(cause);
    }

}
