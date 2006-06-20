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

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.FileIOTest;

public class RollbackTest extends FileIOTest {

    private Prevayler<AppendingSystem> _prevayler;

    public void testRollback() throws Exception {
        testRollback(PrevaylerFactory.createPrevayler(new AppendingSystem(), _testDirectory));
        testRollback(PrevaylerFactory.createTransientPrevayler(new AppendingSystem()));
    }

    private void testRollback(Prevayler<AppendingSystem> prevayler) throws Exception {
        _prevayler = prevayler;

        append("a", "a");

        try {
            append("rollback", "ignored");
            fail("RuntimeException expected and not thrown.");
        } catch (RuntimeException rx) {
        }

        append("b", "ab");

        _prevayler.close();
    }

    private void append(String appendix, String expectedResult) throws Exception {
        _prevayler.execute(new Appendix(appendix));
        assertEquals(expectedResult, system().value());
    }

    private AppendingSystem system() {
        return _prevayler.prevalentSystem();
    }

}
