package org.prevayler.implementation;

import java.io.Serializable;
import java.util.Date;

import junit.framework.TestCase;

import org.prevayler.Prevayler;
import org.prevayler.Transaction;
import org.prevayler.util.PrevaylerFactory;


public class TransactionExecutionTest extends TestCase {

	private Prevayler prevayler;


	protected void setUp() throws Exception {
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

	private void assertState(String expected) {
		String result = ((AppendingSystem)prevayler.prevalentSystem()).value();
		assertEquals(expected, result);
	}

	private void append(String appendix) {
		prevayler.execute(new Appendix(appendix));
	}

}

class AppendingSystem implements Serializable {
		
	private String value = "";
		
	String value() {
		return value;
	}
		
	void append(String appendix) {
		value = value + appendix;
	}
		
}
	
class Appendix implements Transaction {
		
	private final String appendix;
		
	public void executeOn(Object prevalentSystem, Date ignoredToo) {
		((AppendingSystem)prevalentSystem).append(appendix);
	}

	Appendix(String appendix) {
		this.appendix = appendix;
	}
}
