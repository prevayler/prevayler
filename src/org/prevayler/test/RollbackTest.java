package org.prevayler.test;

import org.prevayler.implementation.RollbackPrevayler;
import org.prevayler.implementation.TransactionRolledbackException;

public class RollbackTest {
    static private RollbackPrevayler prevayler;
    private static String prevaylerBase;

    public static void run() throws Exception {
        prevaylerBase = prevaylerBase();

        prevayler = new RollbackPrevayler(new AddingSystem(), prevaylerBase);
        add(10, 10);
        add(20, 30);
        try {
            addRollback(30, 30);
            throw new RuntimeException("Exception expected");
        } catch (TransactionRolledbackException e) {
        }
        add(30, 60);

        prevayler = new RollbackPrevayler(new AddingSystem(), prevaylerBase);
        verify(60);
        try {
            addRollback(30, 50);
            throw new RuntimeException("Exception expected");
        } catch (TransactionRolledbackException e) {
        }
        add(10, 70);
    }

    private static void addRollback(int value, int expectedTotal) throws Exception {
        prevayler.execute(new RollbackAddition(value));
        verify(expectedTotal);
    }

    static private void add(long value, long expectedTotal) throws Exception {
        prevayler.execute(new Addition(value));
        verify(expectedTotal);
    }

    private static void verify(long expectedTotal) {
        if (expectedTotal != system().total()) {
            throw new RuntimeException("Expected " + expectedTotal + " but was " + system().total());
        }
    }

    private static AddingSystem system() {
        return (AddingSystem) prevayler.prevalentSystem();
    }

    private static String prevaylerBase() {
        return "PrevalenceBase" + System.currentTimeMillis();
    }
}
