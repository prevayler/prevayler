//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;
import org.prevayler.implementation.snapshot.XStreamSnapshotManagerTest;
import org.prevayler.implementation.snapshot.JavaSnapshotManagerTest;
import org.prevayler.implementation.snapshot.SkaringaSnapshotManagerTest;

public class Suite extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(PrevaylerFactoryTest.class);
		suite.addTestSuite(TransientPrevaylerTest.class);
		suite.addTestSuite(QueryExecutionTest.class);
		suite.addTestSuite(PersistenceTest.class);
		suite.addTestSuite(LogFileRollingTest.class);
		suite.addTestSuite(CheckpointTest.class);
		suite.addTestSuite(RollbackTest.class);
		suite.addTestSuite(ReplicationTest.class);
		suite.addTestSuite(DurableOutputStreamTest.class);
		suite.addTestSuite(XStreamSnapshotManagerTest.class);
		suite.addTestSuite(JavaSnapshotManagerTest.class);
		suite.addTestSuite(SkaringaSnapshotManagerTest.class);
		return suite;
	}
}
