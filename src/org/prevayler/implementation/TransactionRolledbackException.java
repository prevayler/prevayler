package org.prevayler.implementation;

import java.io.IOException;

public class TransactionRolledbackException extends IOException {
    public TransactionRolledbackException(Exception e) {
        initCause(e);
    }
}
