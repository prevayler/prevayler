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

	/** Transactions cannot directly reference the prevalent system nor any business object because, after serialization, the transaction will have a new copy instead of a reference to the actual object. This is also known as the Prevalence baptism problem because almost all newbies come across it. The point of this test is to make sure that the transient Prevayler implementation also produces this behaviour. */
	public void testBaptismProblem() {
		appendWithDirectReference();
		
		//Must uncomment this next line:
		//assertState("");   //The system state cannot change.
	}


	private void assertState(String expected) {
		String result = ((AppendingSystem)prevayler.prevalentSystem()).value();
		assertEquals(expected, result);
	}

	private void append(String appendix) {
		prevayler.execute(new Appendix(appendix));
	}

	private void appendWithDirectReference() {
		prevayler.execute(new DirectReferenceTransaction((AppendingSystem)prevayler.prevalentSystem()));
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

class DirectReferenceTransaction implements Transaction {

	private final AppendingSystem illegalDirectReference;

	public void executeOn(Object ignored, Date ignoredToo) {
		illegalDirectReference.append("anything");
	}

	public DirectReferenceTransaction(AppendingSystem illegalDirectReference) {
		this.illegalDirectReference = illegalDirectReference;
	}

}
