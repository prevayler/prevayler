package org.prevayler.implementation;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class Suite extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(TransactionExecutionTest.class);
		suite.addTestSuite(PersistenceTest.class);
		suite.addTestSuite(RollbackTest.class);
		return suite;
	}
}
