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

import org.prevayler.*;
import org.prevayler.implementation.snapshot.*;

import java.io.*;

import junit.framework.*;

public class TransientPrevaylerTest extends TestCase {

    private Prevayler<AppendingSystem> prevayler;

    @Override protected void setUp() throws Exception {
        prevayler = PrevaylerFactory.createTransientPrevayler(new AppendingSystem());
    }

    @Override protected void tearDown() throws Exception {
        prevayler = null;
    }

    public void testTransactionExecution() {
        assertState("");

        append("a");
        assertState("a");

        append("b");
        append("c");
        assertState("abc");
    }

    public void testSnapshotAttempt() throws IOException {
        try {
            prevayler.takeSnapshot();
            fail("IOException expected.");
        } catch (SnapshotError expected) {
            assertEquals("Transient Prevaylers are unable to take snapshots.", expected.getMessage());
        }
    }

    private void assertState(String expected) {
        assertEquals(expected, prevayler.execute(new ValueQuery()));
    }

    private void append(String appendix) {
        prevayler.execute(new Appendix(appendix));
    }

}
