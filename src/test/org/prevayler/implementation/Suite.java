package org.prevayler.implementation;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class Suite extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(PrevaylerFactoryTest.class);
		suite.addTestSuite(TransientPrevaylerTest.class);
		suite.addTestSuite(QueryExecutionTest.class);
		suite.addTestSuite(PersistenceTest.class);
		suite.addTestSuite(RollbackTest.class);
		suite.addTestSuite(ReplicationTest.class);
		return suite;
	}
}
