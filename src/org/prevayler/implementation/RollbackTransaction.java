package org.prevayler.implementation;

import org.prevayler.Transaction;

public interface RollbackTransaction extends Transaction {
    boolean isRollbackOnly();
}
