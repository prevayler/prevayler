package org.prevayler.implementation;

import java.io.IOException;
import java.io.File;

import junit.framework.TestCase;
import org.prevayler.implementation.CheckpointPrevayler;

public class CheckpointPrevaylerTest extends TestCase {

    public void testSelfResolvment() throws ClassNotFoundException, IOException {

        String prevalenceBase = "prevalence" + System.currentTimeMillis();
        CheckpointPrevayler prevayler = new CheckpointPrevayler(new TestSystem(), prevalenceBase);
        TestSystem system = (TestSystem) prevayler.prevalentSystem();
        prevayler.execute(new TestTransaction(system.list1));
        prevayler.execute(new TestTransaction(system.list2));

        prevayler = new CheckpointPrevayler(new TestSystem(), prevalenceBase);
        system = (TestSystem) prevayler.prevalentSystem();

        assertEquals(1, system.list1.size());
        assertEquals(1, system.list2.size());
    }

    public void testCheckPoint() throws ClassNotFoundException, IOException {

        File prevalenceBase = new File("prevalence" + System.currentTimeMillis());
        CheckpointPrevayler prevayler = new CheckpointPrevayler(new TestSystem(), prevalenceBase.getAbsolutePath());
        TestSystem system = (TestSystem) prevayler.prevalentSystem();

        prevayler.execute(new TestTransaction(system.list2));
        system.list1.add("1");

        assertEquals(1, prevalenceBase.listFiles().length);

        prevayler.checkpoint();

        assertEquals(2, prevalenceBase.listFiles().length);

        prevayler = new CheckpointPrevayler(new TestSystem(), prevalenceBase.getAbsolutePath());
        system = (TestSystem) prevayler.prevalentSystem();

        assertEquals(1, system.list1.size());
        assertEquals(2, prevalenceBase.listFiles().length);

        prevayler.execute(new TestTransaction(system.list2));
        assertEquals(3, prevalenceBase.listFiles().length);
        assertEquals(2, system.list2.size());
    }
}
