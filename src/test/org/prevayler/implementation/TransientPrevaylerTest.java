package org.prevayler.implementation;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;


public class TransientPrevaylerTest extends PrevalenceTest {

	private Prevayler prevayler;


	protected void setUp() throws Exception {
		super.setUp();
		prevayler = PrevaylerFactory.createTransientPrevayler(new AppendingSystem(),_testDirectory);
	}
	
	public void testTransactionExecution() {
		assertState("");

		append("a");
		assertState("a");

		append("b");
		append("c");
		assertState("abc");
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

}
