package org.prevayler.implementation;

import org.prevayler.PrevalenceError;

public class TransactionNotDeserializableError extends PrevalenceError {

    private static final long serialVersionUID = 1L;

    public TransactionNotDeserializableError() {
        super();
    }

    public TransactionNotDeserializableError(String message) {
        super(message);
    }

    public TransactionNotDeserializableError(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionNotDeserializableError(Throwable cause) {
        super(cause);
    }

}
