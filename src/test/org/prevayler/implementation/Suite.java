// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation;

import org.prevayler.implementation.snapshot.GenericSnapshotManagerTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class Suite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(PrevaylerFactoryTest.class);
        suite.addTestSuite(TransientPrevaylerTest.class);
        suite.addTestSuite(QueryExecutionTest.class);
        suite.addTestSuite(PersistenceTest.class);
        suite.addTestSuite(JournalFileRollingTest.class);
        suite.addTestSuite(SkipOldTransactionsTest.class);
        suite.addTestSuite(CheckpointTest.class);
        suite.addTestSuite(RollbackTest.class);
        suite.addTestSuite(ReplicationTest.class);
        suite.addTestSuite(GenericSnapshotManagerTest.class);
        suite.addTestSuite(TransactionWithQueryTest.class);
        suite.addTestSuite(JournalSerializerTest.class);
        suite.addTestSuite(SnapshotSerializerTest.class);
        suite.addTestSuite(ConfusedFoodTasterStressTest.class);
        suite.addTestSuite(DeadKingDeepCopyTest.class);
        return suite;
    }
}
