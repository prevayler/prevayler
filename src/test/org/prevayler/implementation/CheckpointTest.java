//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import java.io.IOException;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.*;


public class CheckpointTest extends FileIOTest {

	private Prevayler _prevayler;

	public void testCheckpoint() throws Exception {

		crashRecover(); //There is nothing to recover at first. A new system will be created.
		crashRecover();
		append("a","a");
		append("b","ab");
		verify("ab");

		crashRecover();
		verify("");

		append("a","a");
		append("b","ab");
		snapshot();
		snapshot();
		verify("ab");

		crashRecover();
		snapshot();
		append("c","abc");
		snapshot();
		append("d","abcd");
		append("e","abcde");
		verify("abcde");

		crashRecover();
		append("d","abcd");
		verify("abcd");

	}

	private void crashRecover() throws Exception {
		out("CrashRecovery.");
		_prevayler = PrevaylerFactory.createCheckpointPrevayler(new AppendingSystem(), _testDirectory);
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

	private static void out(Object obj) {
		if (false) System.out.println(obj);   //Change this line to see what the test is doing.
	}

}
