package org.prevayler.util;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class Suite extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(PrevaylerFactoryTest.class);
		suite.addTestSuite(QueryPrevaylerTest.class);
		return suite;
	}
}
