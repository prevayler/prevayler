package org.prevayler.test;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

/** Tests for new features. They are more concrete than textual proposals for new features. Many will fail, of course.
*/
public class NewFeaturesTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(ReplicationTest.class);
        return suite;
    }
}
