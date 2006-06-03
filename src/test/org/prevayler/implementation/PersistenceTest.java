//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import java.io.File;
import java.io.IOException;
import junit.framework.AssertionFailedError;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.FileManager;

public class PersistenceTest extends FileIOTest {

	private Prevayler _prevayler;
	private String _prevalenceBase;
    
    public void tearDown() throws Exception {
        if (_prevayler != null) {
            _prevayler.close();
        }
        super.tearDown();
    }

	public void testPersistence() throws Exception {

		newPrevalenceBase();

		crashRecover(); //There is nothing to recover at first. A new system will be created.
		crashRecover();
		append("a","a");
		append("b","ab");
		verify("ab");

		crashRecover();
		verify("ab");

		append("c","abc");
		append("d","abcd");
		snapshot();
		snapshot();
		verify("abcd");

		crashRecover();
		snapshot();
		append("e","abcde");
		snapshot();
		append("f","abcdef");
		append("g","abcdefg");
		verify("abcdefg");

		crashRecover();
		append("h","abcdefgh");
		verify("abcdefgh");

		snapshot();
		_prevayler.close();
		File lastSnapshot =   new File(_prevalenceBase, "0000000000000000008.snapshot");
		File lastTransactionLog =   new File(_prevalenceBase, "0000000000000000008.journal");
		newPrevalenceBase();
		FileManager.produceDirectory(_prevalenceBase);
		lastSnapshot.renameTo(new File(_prevalenceBase, "0000000000000000008.snapshot"));  //Moving the file.
		lastTransactionLog.renameTo(new File(_prevalenceBase, "0000000000000000008.journal"));

		crashRecover();
		append("i","abcdefghi");
		append("j","abcdefghij");
		crashRecover();
		append("k","abcdefghijk");
		append("l","abcdefghijkl");
		crashRecover();
		append("m","abcdefghijklm");
		append("n","abcdefghijklmn");
		crashRecover();
		verify("abcdefghijklmn");
	}

    public void testNondeterminsticError() throws Exception {
        newPrevalenceBase();
        crashRecover(); //There is nothing to recover at first. A new system will be created.

        append("a", "a");
        append("b", "ab");
        verify("ab");

        NondeterministicErrorTransaction.armBomb(1);
        try {
            _prevayler.execute(new NondeterministicErrorTransaction("c"));
            fail();
        } catch (AssertionFailedError failed) {
            throw failed;
        } catch (Error expected) {
            assertEquals(Error.class, expected.getClass());
            assertEquals("BOOM!", expected.getMessage());
        }

        try {
            _prevayler.execute(new Appendix("x"));
            fail();
        } catch (AssertionFailedError failed) {
            throw failed;
        } catch (Error expected) {
            assertEquals(Error.class, expected.getClass());
            assertEquals("Prevayler is no longer processing transactions due to an Error thrown from an earlier transaction.", expected.getMessage());
        }

        try {
            _prevayler.execute(new NullQuery());
            fail();
        } catch (AssertionFailedError failed) {
            throw failed;
        } catch (Error expected) {
            assertEquals(Error.class, expected.getClass());
            assertEquals("Prevayler is no longer processing queries due to an Error thrown from an earlier transaction.", expected.getMessage());
        }

        try {
            _prevayler.prevalentSystem();
            fail();
        } catch (AssertionFailedError failed) {
            throw failed;
        } catch (Error expected) {
            assertEquals(Error.class, expected.getClass());
            assertEquals("Prevayler is no longer allowing access to the prevalent system due to an Error thrown from an earlier transaction.", expected.getMessage());
        }

        try {
            _prevayler.takeSnapshot();
            fail();
        } catch (AssertionFailedError failed) {
            throw failed;
        } catch (Error expected) {
            assertEquals(Error.class, expected.getClass());
            assertEquals("Prevayler is no longer allowing snapshots due to an Error thrown from an earlier transaction.", expected.getMessage());
        }

        crashRecover();
        
        // Note that both the transaction that threw the Error and the
        // subsequent transaction *were* journaled, so they get applied
        // successfully on recovery.
        verify("abcx");
    }

    private void crashRecover() throws Exception {
        out("CrashRecovery.");

        if (_prevayler != null) _prevayler.close();

        PrevaylerFactory factory = new PrevaylerFactory();
        factory.configurePrevalentSystem(new AppendingSystem());
        factory.configurePrevalenceDirectory(prevalenceBase());
        factory.configureTransactionFiltering(false);
        _prevayler = factory.create();
    }

	private void snapshot() throws IOException {
		out("Snapshot.");
		_prevayler.takeSnapshot();
	}


	private void append(String appendix, String expectedResult) throws Exception {
		out("Appending " + appendix);
		_prevayler.execute(new Appendix(appendix));
		verify(expectedResult);
	}


	private void verify(String expectedResult) {
		out("Expecting result: " + expectedResult);
		assertEquals(expectedResult, system().value());
	}


	private AppendingSystem system() {
		return (AppendingSystem)_prevayler.prevalentSystem();
	}


	private String prevalenceBase() {
		return _prevalenceBase;
	}


	private void newPrevalenceBase() throws Exception {
		_prevalenceBase = _testDirectory + File.separator + System.currentTimeMillis();
	}


	private static void out(Object obj) {
		if (false) System.out.println(obj);   //Change this line to see what the test is doing.
	}

}
