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

import static org.prevayler.Safety.Level.LEVEL_5_DEEP_COPYING;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Safety;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.implementation.snapshot.SnapshotError;

import java.io.IOException;
import java.io.Serializable;

public class TransientPrevaylerTest extends FileIOTest {

    private Prevayler<AppendingSystem> prevayler;

    @Override protected void setUp() throws Exception {
        super.setUp();
        prevayler = PrevaylerFactory.createTransientPrevayler(new AppendingSystem());
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

    /**
     * The baptism problem occurs when a Transaction keeps a direct reference to
     * a business object instead of querying for it given the Prevalent System.
     */
    public void testFailFastBaptismProblem() {
        append("a");

        AppendingSystem directReference = prevayler.execute(new SystemQuery<AppendingSystem>());
        prevayler.execute(new DirectReferenceTransaction(directReference));

        assertState("a");
    }

    @Override protected void tearDown() throws Exception {
        prevayler = null;
        super.tearDown();
    }

    private void assertState(String expected) {
        assertEquals(expected, prevayler.execute(new ValueQuery()));
    }

    private void append(String appendix) {
        prevayler.execute(new Appendix(appendix));
    }

    @Safety(LEVEL_5_DEEP_COPYING) static private class DirectReferenceTransaction implements GenericTransaction<AppendingSystem, Void, RuntimeException>, Serializable {

        private static final long serialVersionUID = -7885669885494051746L;

        private final AppendingSystem _illegalDirectReference;

        DirectReferenceTransaction(AppendingSystem illegalDirectReference) {
            _illegalDirectReference = illegalDirectReference;
        }

        public Void executeOn(@SuppressWarnings("unused") AppendingSystem prevalentSystem, @SuppressWarnings("unused") PrevalenceContext prevalenceContext) {
            _illegalDirectReference.append("anything");
            return null;
        }

    }

}
