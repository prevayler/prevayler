//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import java.io.IOException;
import java.util.Date;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Transaction;
import org.prevayler.foundation.*;


public class TransientPrevaylerTest extends FileIOTest {

	private Prevayler prevayler;


	protected void setUp() throws Exception {
		super.setUp();
		prevayler = PrevaylerFactory.createTransientPrevayler(new AppendingSystem());
	}


	public void testTransactionExecution() {
		assertState("");

		append("a");
		assertState("a");

		append("b");
		append("c");
		assertState("abc");
	}

	public void testSnapshotAttempt() {
		try {
			prevayler.takeSnapshot();
			fail("IOException expected.");
		} catch (IOException iox) {
			assertEquals("Transient Prevaylers are unable to take snapshots.", iox.getMessage());
		}
	}


	/** The baptism problem occurs when a Transaction keeps a direct reference to a business object instead of querying for it given the Prevalent System. 
	 */
	public void testFailFastBaptismProblem() {
		append("a");

		AppendingSystem directReference = (AppendingSystem)prevayler.prevalentSystem();
		prevayler.execute(new DirectReferenceTransaction(directReference));
		
		assertState("a");
	}

	protected void tearDown() throws Exception {
		prevayler = null;
		super.tearDown();
	}


	private void assertState(String expected) {
		String result = ((AppendingSystem)prevayler.prevalentSystem()).value();
		assertEquals(expected, result);
	}


	private void append(String appendix) {
		prevayler.execute(new Appendix(appendix));
	}



	static private class DirectReferenceTransaction implements Transaction {
	
		private final AppendingSystem _illegalDirectReference;

		DirectReferenceTransaction(AppendingSystem illegalDirectReference) {
			_illegalDirectReference = illegalDirectReference;
		}
	
		public void executeOn(Object ignored, Date ignoredToo) {
			_illegalDirectReference.append("anything");
		}

	}

}
