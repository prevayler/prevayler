package org.prevayler.test;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class AllTestSuite extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(RollbackTest.class);
		suite.addTestSuite(PersistenceTest.class);
		suite.addTestSuite(ClockTest.class);
		suite.addTestSuite(ClockTickLogOptimizationTest.class);
		return suite;
	}
}
