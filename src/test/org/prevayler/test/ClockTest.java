package org.prevayler.test;

import junit.framework.TestCase;
import org.prevayler.Transaction;
import org.prevayler.implementation.SnapshotManager;
import org.prevayler.implementation.SnapshotPrevayler;
import org.prevayler.implementation.log.TransactionLogger;
import org.prevayler.util.clock.ClockActor;
import org.prevayler.util.clock.ClockedSystem;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class ClockTest extends TestCase {
    private SnapshotPrevayler prevayler;
    private static final int TOLERANCE = 1000;
    private ClockActor clockActor;

    public static class ClockedTestSystem extends ClockedSystem {
        private Date date;

        public void date(Date date) {
            this.date = date;
        }

        public Date date() {
            return date;
        }
    }

    public static class ChangeDateTransaction implements Transaction {
        public void executeOn(Object prevalentSystem) {
            ClockedTestSystem clockedTestSystem = (ClockedTestSystem) prevalentSystem;
            clockedTestSystem.date(clockedTestSystem.clock().time());
        }
    }

    protected void setUp() throws Exception {
        File prevalenceBase = File.createTempFile("prevalence", "base");
        prevalenceBase.delete();
        prevalenceBase.mkdirs();
        prevalenceBase.deleteOnExit();
        prevayler = new SnapshotPrevayler(new ClockedTestSystem(),
                new SnapshotManager(prevalenceBase.getAbsolutePath()),
                new TransactionLogger(prevalenceBase.getAbsolutePath()));
        clockActor = new ClockActor(prevayler);
        Thread.sleep(100);
    }

    protected void tearDown() throws Exception {
        clockActor.doStop();
    }

    public void test() throws IOException, ClassNotFoundException, InterruptedException {
        prevayler.execute(new ChangeDateTransaction());
        assertAboutSameDate(new Date(), system().date());
        // uncomment these lines to test clock-tick-optimizations
        // Thread.sleep(1000);
        // assertEquals(2, transactionCount());
    }

    private long transactionCount() {
        return prevayler.transactionCount();
    }

    private void assertAboutSameDate(Date expected, Date observed) {
        assertEquals(expected.getTime() / TOLERANCE, observed.getTime() / TOLERANCE);
    }

    private ClockedTestSystem system() {
        return (ClockedTestSystem) prevayler.prevalentSystem();
    }
}
