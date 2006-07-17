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

import java.io.*;

public class DeadKingDeepCopyTest extends FileIOTest {

    private Prevayler<AppendingSystem> _prevayler;

    private String _prevalenceBase;

    @Override public void tearDown() throws Exception {
        if (_prevayler != null) {
            _prevayler.close();
        }
        super.tearDown();
    }

    public void testDeadKing() throws Exception {
        newPrevalenceBase();
        crashRecover();

        append("a", "a");
        append("b", "ab");
        verify("ab");

        NondeterministicErrorTransaction.armBomb(2);
        try {
            _prevayler.execute(new NondeterministicErrorTransaction("c"));
            fail();
        } catch (Bomb expected) {
            assertEquals("BOOM!", expected.getMessage());
        }

        try {
            _prevayler.execute(new RollbackAppendix("rollback"));
            fail();
        } catch (RuntimeException expected) {
            assertEquals(RuntimeException.class, expected.getClass());
            assertEquals("Testing Rollback", expected.getMessage());
        }

        try {
            _prevayler.execute(new RollbackAppendix("z"));
            fail();
        } catch (ErrorInEarlierTransactionError expected) {
        }

        crashRecover();
        verify("abc");
    }

    private void crashRecover() throws Exception {
        if (_prevayler != null)
            _prevayler.close();
        _prevayler = PrevaylerFactory.createPrevayler(new AppendingSystem(), prevalenceBase());
    }

    private void append(String appendix, String expectedResult) throws Exception {
        _prevayler.execute(new RollbackAppendix(appendix));
        verify(expectedResult);
    }

    private void verify(String expectedResult) {
        assertEquals(expectedResult, _prevayler.execute(new ValueQuery()));
    }

    private String prevalenceBase() {
        return _prevalenceBase;
    }

    private void newPrevalenceBase() throws Exception {
        _prevalenceBase = _testDirectory + File.separator + System.currentTimeMillis();
    }

}
