package org.prevayler.implementation;

import junit.framework.TestCase;

import java.io.File;

import org.prevayler.Prevayler;

public class RollbackTest extends TestCase {
    static private Prevayler prevayler;
    private static String prevalenceBase;

    protected void setUp() throws Exception {
        super.setUp();

        File tempFile = File.createTempFile("Prevalence", "Base");
        tempFile.delete();
        tempFile.mkdirs();
        tempFile.deleteOnExit();
        prevalenceBase = tempFile.getAbsolutePath();
    }

    protected void tearDown() throws Exception {
        delete(prevalenceBase);
    }

    public void testRollback() throws Exception {
        prevayler = PrevaylerFactory.createPrevayler(new AddingSystem(), prevalenceBase);
		add(10, 10);
        addRollback(1, 10);
		add(1, 11);
    }

    private void addRollback(int value, int expectedTotal) throws Exception {
    	boolean isThrown = false;
		try {
			// TODO Simplify: Eliminate RollbackAddition and make it a special case of addition like the demo.
	        prevayler.execute(new RollbackAddition(value));
		} catch (RuntimeException e) {
			isThrown = true;
		}
		if (!isThrown) throw new RuntimeException("RuntimeException expected and not thrown.");
        assertEquals(expectedTotal, system().total());
    }

    private void add(long value, long expectedTotal) throws Exception {
        prevayler.execute(new Addition(value));
        assertEquals(expectedTotal, system().total());
    }

    private AddingSystem system() {
        return (AddingSystem) prevayler.prevalentSystem();
    }

    public static void delete(String dir) {
        delete(new File(dir));
    }

    public static void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File sub = files[i];
                    delete(sub);
                }
            }
        }
        file.delete();
    }
}
