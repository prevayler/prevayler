package org.prevayler.implementation;

import java.io.IOException;

import junit.framework.TestCase;
import org.prevayler.Transaction;

public class SnapshotPrevaylerTest extends TestCase {
    public void testIgnoreStartupExceptions() throws IOException, ClassNotFoundException {

        String prevalenceBase = "PrevalenceBase" + System.currentTimeMillis();
        SnapshotPrevayler prevayler = new SnapshotPrevayler(new TestSystem(), prevalenceBase);

        try {
            prevayler.execute(new FailingTransaction());
            fail("Should get exception");
        } catch (IllegalStateException e) {}

        try {
            prevayler.execute(new FailingTransaction());
            fail("Should get exception");
        } catch (IllegalStateException e) {}

        assertEquals(2, ((TestSystem) prevayler.prevalentSystem()).list1.size());

        prevayler = new SnapshotPrevayler(new TestSystem(), prevalenceBase);
        assertEquals(2, ((TestSystem) prevayler.prevalentSystem()).list1.size());

        try {
            prevayler.execute(new FailingTransaction());
            fail("Should get exception");
        } catch (IllegalStateException e) {}
    }

    public static class FailingTransaction implements Transaction {
        public void executeOn(Object prevalentSystem) {
            ((TestSystem) prevalentSystem).list1.add("test");
            throw new IllegalStateException("fail");
        }
    }
}
