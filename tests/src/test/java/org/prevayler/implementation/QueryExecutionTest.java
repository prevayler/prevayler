//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Query;
import org.prevayler.TransactionWithQuery;
import org.prevayler.foundation.FileIOTest;

import java.util.Date;
import java.util.LinkedList;


public class QueryExecutionTest extends FileIOTest {

  public void testQuery() throws Exception {
    LinkedList<String> prevalentSystem = new LinkedList<String>();
    Prevayler<LinkedList<String>> prevayler = PrevaylerFactory.createTransientPrevayler(prevalentSystem);
    Integer result = prevayler.execute(query());
    assertEquals(0, result.intValue());
  }

  private static Query<LinkedList<String>, Integer> query() {
    return new Query<LinkedList<String>, Integer>() {
      private static final long serialVersionUID = -9053395276292652684L;

      public Integer query(LinkedList<String> prevalentSystem, Date ignored) throws Exception {
        return prevalentSystem.size();
      }
    };
  }

  public void testTransactionWithQuery() throws Exception {
    LinkedList<String> prevalentSystem = new LinkedList<String>();
    Prevayler<LinkedList<String>> prevayler = PrevaylerFactory.createTransientPrevayler(prevalentSystem);
    String result = prevayler.execute(transactionWithQuery());
    assertEquals("abc", result);
    assertEquals("added element", prevalentSystem.get(0));
  }

  private static TransactionWithQuery<LinkedList<String>, String> transactionWithQuery() {
    return new TransactionWithQuery<LinkedList<String>, String>() {
      private static final long serialVersionUID = -2976662596936807721L;

      public String executeAndQuery(LinkedList<String> prevalentSystem, Date timestamp) {
        prevalentSystem.add("added element");
        return "abc";
      }
    };
  }

}
