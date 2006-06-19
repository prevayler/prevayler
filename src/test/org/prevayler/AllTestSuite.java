// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(org.prevayler.implementation.Suite.suite());
        suite.addTest(org.prevayler.foundation.Suite.suite());
        return suite;
    }
}
