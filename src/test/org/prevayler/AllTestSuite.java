package org.prevayler;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class AllTestSuite extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(org.prevayler.implementation.Suite.suite());
		suite.addTest(org.prevayler.util.Suite.suite());
		return suite;
	}
}
