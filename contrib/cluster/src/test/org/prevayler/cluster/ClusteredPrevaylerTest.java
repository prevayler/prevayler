package org.prevayler.cluster;

import java.io.Serializable;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import junit.framework.TestCase;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Transaction;
import org.prevayler.cluster.ClusteredPrevayler;

public class ClusteredPrevaylerTest extends TestCase {
    private ClusteredPrevayler prevayler1;
    private ClusteredPrevayler prevayler2;
    private PrevaylerFactory factory;
    private File prevalenceBase;

    protected void setUp() throws Exception {
        super.setUp();
        prevalenceBase = createTempDirectory();
        factory = new PrevaylerFactory();
        factory.configureTransientMode(true);
        factory.configurePrevalentSystem(new ListSystem());
        prevayler1 = createPrevayler("1");
        prevayler2 = createPrevayler("2");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        prevayler1.close();
        prevayler2.close();
    }


    public void testReplicationOfTransactions() throws Exception {
        Transaction transaction = new AddToList("Hello World");
        prevayler1.execute(transaction);
        assertAllSystemsEqual();
    }

    public void testMultipleTransactionExecutions() throws Exception {
        Transaction transaction = new AddToList("And once more");
        prevayler2.execute(transaction);
        assertAllSystemsEqual();

        prevayler2.execute(transaction);
        assertAllSystemsEqual();
    }

    public void testSync() throws Exception {
        prevayler2.close();
        prevayler1.execute(new AddToList("Hello World"));
        prevayler2 = createPrevayler("2");
        assertAllSystemsEqual();
        prevayler2.execute(new AddToList("And some more worlds..."));
        assertAllSystemsEqual();
        prevayler1.close();
        prevayler1 = createPrevayler("1");
        assertAllSystemsEqual();
        assertEquals("Hello WorldAnd some more worlds...", ((ListSystem) prevayler1.prevalentSystem()).text.toString());
    }

    public void testRestartingClusterWillKeepData() throws Exception {
        prevayler1.close(); prevayler2.close();
        factory.configureTransientMode(false);
        prevayler1 = createPrevayler("1");
        prevayler2 = createPrevayler("2");

        prevayler1.execute(new AddToList("Hello world is persistent"));
        assertNotNull(new File(prevalenceBase("new1")).list());
        assertEquals("Hello world is persistent", ((ListSystem) prevayler1.prevalentSystem()).text.toString());
        assertAllSystemsEqual();

        prevayler1.close(); prevayler2.close();

        prevayler1 = createPrevayler("1");
        prevayler2 = createPrevayler("2");

        assertEquals("Hello world is persistent", ((ListSystem) prevayler1.prevalentSystem()).text.toString());
        assertAllSystemsEqual();
    }


    public void testLocking() throws Exception {
        Thread t1 = createAddToListThread(prevayler1, "1");
        Thread t2 = createAddToListThread(prevayler1, "2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        assertEquals(20, ((ListSystem) prevayler1.prevalentSystem()).text.length());
        assertAllSystemsEqual();
        System.out.println(prevayler1.prevalentSystem());
    }

    private Thread createAddToListThread(final Prevayler prevayler, final String string) {
        return new Thread() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    prevayler.execute(new AddToList(string));
                }
            }
        };
    }

    private void assertAllSystemsEqual() {
        assertEquals(prevayler1.prevalentSystem(), prevayler2.prevalentSystem());
        assertEquals(prevayler1.clock().time(), prevayler2.clock().time());
        assertObjectsInSystemNotSame();
    }

    private void assertObjectsInSystemNotSame() {
        assertNotSame(((ListSystem) prevayler1.prevalentSystem()).text, ((ListSystem) prevayler2.prevalentSystem()).text);
    }

    private ClusteredPrevayler createPrevayler(String name) throws Exception {
        factory.configurePrevalentSystem(new ListSystem());
        return new ClusteredPrevayler(factory, prevalenceBase("old" + name), prevalenceBase("new" + name));
    }

    private String prevalenceBase(String name) {
        return new File(prevalenceBase, name).getAbsolutePath();
    }

    private File createTempDirectory() throws IOException {
        File dir = File.createTempFile("prevalenceBase", ".tmp");
        dir.delete();
        dir.mkdirs();
        return dir;
    }

    private static class AddToList implements Transaction {
        private final String string;

        public AddToList(String string) {
            this.string = string;
        }

        public void executeOn(Object prevalentSystem, Date executionTime) {
            ListSystem list = (ListSystem) prevalentSystem;
            list.add(string, executionTime);
        }
    }

    private static class ListSystem implements Serializable {
        private StringBuffer text = new StringBuffer();

        public void add(String string, Date executionTime) {
            text.append(string);
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ListSystem)) return false;

            final ListSystem clockedList = (ListSystem) o;

            if (!text.toString().equals(clockedList.text.toString())) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (text != null ? text.hashCode() : 0);
            return result;
        }

        public String toString() {
            return "ClockedList@" + hashCode() + " text: " + text;
        }
    }
}
