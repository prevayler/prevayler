package org.prevayler.test;

import org.prevayler.test.old.OldTestSuite;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class AllTestSuite extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(TransactionExecutionTest.class);
		suite.addTest(OldTestSuite.suite());
		return suite;
	}
}
