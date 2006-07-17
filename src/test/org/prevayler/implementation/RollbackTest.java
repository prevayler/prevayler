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
import org.prevayler.foundation.*;

public class RollbackTest extends FileIOTest {

    private Prevayler<AppendingSystem> _prevayler;

    public void testRollback() throws Exception {
        testRollback(PrevaylerFactory.createPrevayler(new AppendingSystem(), _testDirectory));
        testRollback(PrevaylerFactory.createTransientPrevayler(new AppendingSystem()));
    }

    private void testRollback(Prevayler<AppendingSystem> prevayler) throws Exception {
        _prevayler = prevayler;

        _prevayler.execute(new Appendix("a"));
        assertEquals("a", _prevayler.execute(new ValueQuery()));

        try {
            _prevayler.execute(new RollbackAppendix("<rollback>"));
            fail();
        } catch (RuntimeException rx) {
            assertEquals("Testing Rollback", rx.getMessage());
        }

        _prevayler.execute(new Appendix("b"));
        assertEquals("ab", _prevayler.execute(new ValueQuery()));

        try {
            _prevayler.execute(new Appendix("<rollback>"));
            fail();
        } catch (RuntimeException rx) {
            assertEquals("Testing Rollback", rx.getMessage());
        }

        _prevayler.execute(new Appendix("c"));
        assertEquals("ab<rollback>c", _prevayler.execute(new ValueQuery()));

        _prevayler.close();
    }

}
