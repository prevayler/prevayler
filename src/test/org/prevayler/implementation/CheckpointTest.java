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

import java.io.IOException;

public class CheckpointTest extends FileIOTest {

    private Prevayler<AppendingSystem> _prevayler;

    public void testCheckpoint() throws Exception {

        crashRecover(); // There is nothing to recover at first. A new system
        // will be created.
        crashRecover();
        append("a", "a");
        append("b", "ab");
        verify("ab");

        crashRecover();
        verify("");

        append("a", "a");
        append("b", "ab");
        snapshot();
        snapshot();
        verify("ab");

        crashRecover();
        snapshot();
        append("c", "abc");
        snapshot();
        append("d", "abcd");
        append("e", "abcde");
        verify("abcde");

        crashRecover();
        append("d", "abcd");
        verify("abcd");

    }

    private void crashRecover() throws Exception {
        _prevayler = PrevaylerFactory.createCheckpointPrevayler(new AppendingSystem(), _testDirectory);
    }

    private void snapshot() throws IOException {
        _prevayler.takeSnapshot();
    }

    private void append(String appendix, String expectedResult) throws Exception {
        _prevayler.execute(new Appendix(appendix));
        verify(expectedResult);
    }

    private void verify(String expectedResult) {
        assertEquals(expectedResult, _prevayler.execute(new ValueQuery()));
    }

}
