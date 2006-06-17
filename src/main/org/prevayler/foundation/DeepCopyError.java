package org.prevayler.foundation;

import org.prevayler.PrevalenceError;

public class DeepCopyError extends PrevalenceError {

    private static final long serialVersionUID = 1L;

    public DeepCopyError() {
        super();
    }

    public DeepCopyError(String message) {
        super(message);
    }

    public DeepCopyError(String message, Throwable cause) {
        super(message, cause);
    }

    public DeepCopyError(Throwable cause) {
        super(cause);
    }

}
