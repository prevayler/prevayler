//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.prevayler.foundation.ChunkingTest;
import org.prevayler.foundation.DurableOutputStreamTest;
import org.prevayler.foundation.FileLockerTest;
import org.prevayler.foundation.gzip.MultiMemberGZIPTest;
import org.prevayler.implementation.snapshot.GenericSnapshotManagerTest;

public class Suite extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(PrevaylerFactoryTest.class);
		suite.addTestSuite(TransientPrevaylerTest.class);
		suite.addTestSuite(QueryExecutionTest.class);
		suite.addTestSuite(PersistenceTest.class);
		suite.addTestSuite(JournalFileRollingTest.class);
		suite.addTestSuite(SkipOldTransactionsTest.class);
		suite.addTestSuite(ChunkingTest.class);
		suite.addTestSuite(CheckpointTest.class);
		suite.addTestSuite(RollbackTest.class);
		suite.addTestSuite(ReplicationTest.class);
		suite.addTestSuite(DurableOutputStreamTest.class);
		suite.addTestSuite(GenericSnapshotManagerTest.class);
		suite.addTestSuite(TransactionWithQueryTest.class);
		suite.addTestSuite(JournalSerializerTest.class);
		suite.addTestSuite(SnapshotSerializerTest.class);
		suite.addTestSuite(MultiMemberGZIPTest.class);
		suite.addTestSuite(FileLockerTest.class);
		suite.addTestSuite(ConfusedFoodTasterTest.class);
		suite.addTestSuite(ConfusedFoodTasterStressTest.class);
		return suite;
	}
}
