// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.backward;

import org.prevayler.*;
import org.prevayler.foundation.*;
import org.prevayler.implementation.*;

@SuppressWarnings({ "deprecation", "unchecked" }) public class BackwardCompatibilityTest extends FileIOTest {

    private Prevayler<AppendingSystem> _prevayler;

    public void testRollback() throws Exception {
        _prevayler = PrevaylerFactory.createPrevayler(new AppendingSystem(), _testDirectory);

        _prevayler.execute(new BackwardAppendix("a"));
        assertEquals("a", _prevayler.execute(new ValueQuery()));

        try {
            _prevayler.execute(new BackwardAppendix("<rollback>"));
            fail();
        } catch (RuntimeException rx) {
            assertEquals("Testing Rollback", rx.getMessage());
        }

        _prevayler.execute(new BackwardAppendix("b"));
        assertEquals("ab", _prevayler.execute(new ValueQuery()));

        _prevayler.close();
    }

    public void testNonRollback() throws Exception {
        PrevaylerFactory<AppendingSystem> factory = new PrevaylerFactory<AppendingSystem>();
        factory.configurePrevalentSystem(new AppendingSystem());
        factory.configurePrevalenceDirectory(_testDirectory);
        factory.configureTransactionFiltering(false);
        _prevayler = factory.create();

        _prevayler.execute(new BackwardAppendix("a"));
        assertEquals("a", _prevayler.execute(new ValueQuery()));

        try {
            _prevayler.execute(new BackwardAppendix("<rollback>"));
            fail();
        } catch (RuntimeException rx) {
            assertEquals("Testing Rollback", rx.getMessage());
        }

        _prevayler.execute(new BackwardAppendix("b"));
        assertEquals("a<rollback>b", _prevayler.execute(new ValueQuery()));

        _prevayler.close();
    }

}
