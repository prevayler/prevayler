package org.prevayler.test.old;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class OldTestSuite extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(RollbackTest.class);
		suite.addTestSuite(PersistenceTest.class);
		return suite;
	}
}
