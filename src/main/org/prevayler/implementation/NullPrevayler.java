package org.prevayler.implementation;

import org.prevayler.Prevayler;
import org.prevayler.Transaction;

public class NullPrevayler implements Prevayler {
    private Object system;

    public NullPrevayler(Object system) {
        this.system = system;
    }

    public void execute(Transaction transaction) {
        transaction.executeOn(system);
    }

    public Object prevalentSystem() {
        return system;
    }
}
