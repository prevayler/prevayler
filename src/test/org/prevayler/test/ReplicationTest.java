package org.prevayler.test;

import org.prevayler.implementation.*;
import junit.framework.TestCase;

public class ReplicationTest extends TestCase {

	public void testNormalConditions() {
		connectionUp();
		serverUp();

		serverTransaction("a");
		serverTransaction("b");
		serverAssert("ab");

		clientUp();
		clientAssert("ab");

		serverTransaction("c");
		bothAssert("abc");

		clientTransaction("d");
		bothAssert("abcd");

		clientTransaction("e");
		bothAssert("abcde");

		serverTransaction("f");
		serverTransaction("g");
		clientTransaction("h");
		clientTransaction("i");
		serverTransaction("j");
		clientTransaction("k");
		bothAssert("abcdefghijk");
	}

	private void connectionUp() {toDo();}
	private void connectionDown() {toDo();}
	private void serverUp() {toDo();}
	private void serverDown() {toDo();}
	private void clientUp() {toDo();}
	private void clientDown() {toDo();}

	private void serverTransaction(String appendix) {toDo();}
	private void clientTransaction(String appendix) {toDo();}

	private void serverAssert(String expected) {toDo();}
	private void clientAssert(String expected) {toDo();}
	private void bothAssert(String expected) {
		serverAssert(expected);
		clientAssert(expected);
	}

	private void toDo() {
		throw new RuntimeException("New test to be implemented.");
	}

}
