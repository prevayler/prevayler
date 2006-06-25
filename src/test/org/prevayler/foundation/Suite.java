// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.foundation;

import org.prevayler.foundation.gzip.MultiMemberGZIPTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class Suite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(DeepCopierTest.class);
        suite.addTestSuite(ChunkingTest.class);
        suite.addTestSuite(DurableOutputStreamTest.class);
        suite.addTestSuite(MultiMemberGZIPTest.class);
        suite.addTestSuite(FileLockerTest.class);
        return suite;
    }

}
