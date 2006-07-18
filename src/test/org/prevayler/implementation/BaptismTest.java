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

import static org.prevayler.Safety.Journaling.*;
import static org.prevayler.Safety.Locking.*;

import org.prevayler.*;

import java.io.*;

import junit.framework.*;

/**
 * The baptism problem occurs when a Transaction keeps a direct reference to a
 * business object instead of querying for it given the Prevalent System.
 */
public class BaptismTest extends TestCase {

    private Prevayler<AppendingSystem> prevayler;

    @Override protected void setUp() throws Exception {
        prevayler = PrevaylerFactory.createTransientPrevayler(new AppendingSystem());
    }

    @Override protected void tearDown() throws Exception {
        prevayler = null;
    }

    public void testBaptismProblemDemonstrated() {
        append("a");

        AppendingSystem directReference = prevayler.execute(new SystemQuery<AppendingSystem>());
        prevayler.execute(new DirectReferenceTransactionWithoutDeserialization(directReference));

        assertState("axxx");
    }

    public void testBaptismProblemFixed() {
        append("a");

        AppendingSystem directReference = prevayler.execute(new SystemQuery<AppendingSystem>());
        prevayler.execute(new DirectReferenceTransactionWithDeserialization(directReference));

        assertState("a");
    }

    private void assertState(String expected) {
        assertEquals(expected, prevayler.execute(new ValueQuery()));
    }

    private void append(String appendix) {
        prevayler.execute(new Appendix(appendix));
    }

    @Safety(journaling = PERSISTENT, locking = EXCLUSIVE) private static class DirectReferenceTransactionWithoutDeserialization implements GenericTransaction<AppendingSystem, Void, RuntimeException>, Serializable {

        private static final long serialVersionUID = 1L;

        private final AppendingSystem _illegalDirectReference;

        DirectReferenceTransactionWithoutDeserialization(AppendingSystem illegalDirectReference) {
            _illegalDirectReference = illegalDirectReference;
        }

        public Void executeOn(@SuppressWarnings("unused") PrevalenceContext<? extends AppendingSystem> prevalenceContext) {
            _illegalDirectReference.append("xxx");
            return null;
        }

    }

    @Safety(journaling = DESERIALIZE_BEFORE_EXECUTION, locking = EXCLUSIVE) private static class DirectReferenceTransactionWithDeserialization implements GenericTransaction<AppendingSystem, Void, RuntimeException>, Serializable {

        private static final long serialVersionUID = 1L;

        private final AppendingSystem _illegalDirectReference;

        DirectReferenceTransactionWithDeserialization(AppendingSystem illegalDirectReference) {
            _illegalDirectReference = illegalDirectReference;
        }

        public Void executeOn(@SuppressWarnings("unused") PrevalenceContext<? extends AppendingSystem> prevalenceContext) {
            _illegalDirectReference.append("xxx");
            return null;
        }

    }

}
