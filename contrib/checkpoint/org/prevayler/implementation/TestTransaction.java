package org.prevayler.implementation;

import java.util.List;

import org.prevayler.Transaction;

class TestTransaction implements Transaction {
    private List list;

    public TestTransaction(List list) {
        this.list = list;
    }

    public void executeOn(Object prevalentSystem) {
        list.add("stuff");
    }
}
