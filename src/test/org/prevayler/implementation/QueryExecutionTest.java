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

import static org.prevayler.Safety.Level.LEVEL_1_SHARED_LOCKING;
import static org.prevayler.Safety.Level.LEVEL_4_JOURNALING;

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Safety;
import org.prevayler.foundation.FileIOTest;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class QueryExecutionTest extends FileIOTest {

    public void testQuery() throws Exception {
        List<String> prevalentSystem = new LinkedList<String>();
        Prevayler<List<String>> prevayler = PrevaylerFactory.createTransientPrevayler(prevalentSystem);
        assertEquals(0, (int) prevayler.execute(new MyQuery()));
    }

    public void testTransactionWithQuery() throws Exception {
        List prevalentSystem = new LinkedList();
        Prevayler<List> prevayler = PrevaylerFactory.createTransientPrevayler(prevalentSystem);
        assertEquals("abc", prevayler.execute(new MyTransaction()));
        assertEquals("added element", prevalentSystem.get(0));
    }

    @Safety(LEVEL_1_SHARED_LOCKING) private static final class MyQuery implements GenericTransaction<List<String>, Integer, RuntimeException> {
        private static final long serialVersionUID = 1L;

        public Integer executeOn(List<String> prevalentSystem, @SuppressWarnings("unused") PrevalenceContext prevalenceContext) {
            return prevalentSystem.size();
        }
    }

    @Safety(LEVEL_4_JOURNALING) private static final class MyTransaction implements GenericTransaction<List<String>, String, RuntimeException>, Serializable {
        private static final long serialVersionUID = -2976662596936807721L;

        public String executeOn(List<String> prevalentSystem, @SuppressWarnings("unused") PrevalenceContext prevalenceContext) {
            prevalentSystem.add("added element");
            return "abc";
        }
    }

}
