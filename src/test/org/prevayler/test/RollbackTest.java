package org.prevayler.test;

import org.prevayler.implementation.RollbackPrevayler;

public class RollbackTest {
    static private RollbackPrevayler prevayler;
    private static String prevaylerBase;

    public static void run() throws Exception {
        prevaylerBase = prevaylerBase();

        prevayler = new RollbackPrevayler(new AddingSystem(), prevaylerBase);
        add(10, 10);
        add(20, 30);
        addRollback(30, 30);
        add(30, 60);

        prevayler = new RollbackPrevayler(new AddingSystem(), prevaylerBase);
        verify(60);
        addRollback(30, 60);
        add(10, 70);
    }

    private static void addRollback(int value, int expectedTotal) throws Exception {
    	boolean isThrown = false;
		try {
	        prevayler.execute(new RollbackAddition(value));
		} catch (RuntimeException e) {
			isThrown = true;
		}
		if (!isThrown) throw new RuntimeException("RuntimeException expected and not thrown.");
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
