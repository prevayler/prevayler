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
import org.prevayler.Query;
import org.prevayler.Transaction;
import org.prevayler.foundation.FileIOTest;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class QueryExecutionTest extends FileIOTest {

    public void testQuery() throws Exception {
        List<String> prevalentSystem = new LinkedList<String>();
        Prevayler<List<String>> prevayler = PrevaylerFactory.createTransientPrevayler(prevalentSystem);
        assertEquals(0, (int) prevayler.execute(query()));
    }

    private static Query<List<String>, Integer, RuntimeException> query() {
        return new Query<List<String>, Integer, RuntimeException>() {
            public Integer query(List<String> prevalentSystem, @SuppressWarnings("unused") Date ignored) {
                return prevalentSystem.size();
            }
        };
    }

    public void testTransactionWithQuery() throws Exception {
        List prevalentSystem = new LinkedList();
        Prevayler<List> prevayler = PrevaylerFactory.createTransientPrevayler(prevalentSystem);
        assertEquals("abc", prevayler.execute(new TransactionWithQuery()));
        assertEquals("added element", prevalentSystem.get(0));
    }

    private static class TransactionWithQuery implements Transaction<List<String>, String, RuntimeException>, Serializable {
        private static final long serialVersionUID = -2976662596936807721L;

        public String executeOn(List<String> prevalentSystem, @SuppressWarnings("unused") Date timestamp) {
            prevalentSystem.add("added element");
            return "abc";
        }
    }

}
