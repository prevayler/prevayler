package org.prevayler.util.clock;

import java.util.Date;

import junit.framework.TestCase;
import org.prevayler.util.clock.ClockedSystem;
import org.prevayler.util.clock.ClockedTransaction;

import org.prevayler.implementation.TransientPrevayler;

public class ClockedTransactionTest extends TestCase {
    /**
     * Sorry, it had to be static... Couldn't help it, tried my best. If you can make it non-static, please do so.
     */
    private static Date executedTime;

    private ClockedSystem prevalentSystem;
    private TransientPrevayler prevayler;

    protected void setUp() throws Exception {
        super.setUp();

        newPrevayler();
    }

    private void newPrevayler() {
        prevalentSystem = new AbstractClockedSystem() {};
        prevayler = new TransientPrevayler(prevalentSystem);
    }

    public void testExecute() throws Exception {

        ClockedTransaction clockedTransaction = new MyClockedTransaction();
        assertNull(clockedTransaction.timeOfExecution);

        clockedTransaction.executeUsing(prevayler);
        assertNotNull(clockedTransaction.timeOfExecution);
        assertNotNull(executedTime);
        assertEquals(clockedTransaction.timeOfExecution, executedTime);

        newPrevayler();

        Date previousExecutedTime = executedTime;
        clockedTransaction.executeOn(prevalentSystem);
        assertEquals(previousExecutedTime, executedTime);
    }

    public static class MyClockedTransaction extends ClockedTransaction {
        public Object executeClocked(ClockedSystem clockedSystem) {
            executedTime = clockedSystem.clock().time();
            return null;
        }
    }
}
