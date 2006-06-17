package org.prevayler.implementation;

import org.prevayler.PrevalenceError;

public class ImpossibleError extends PrevalenceError {

    private static final long serialVersionUID = 1L;

    public ImpossibleError() {
        super();
    }

    public ImpossibleError(String message) {
        super(message);
    }

    public ImpossibleError(String message, Throwable cause) {
        super(message, cause);
    }

    public ImpossibleError(Throwable cause) {
        super(cause);
    }

}
