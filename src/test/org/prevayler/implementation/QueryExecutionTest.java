//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Query;
import org.prevayler.TransactionWithQuery;
import org.prevayler.foundation.*;


public class QueryExecutionTest extends FileIOTest {

	public void testQuery() throws Exception {
		List prevalentSystem = new LinkedList();
		Prevayler prevayler = PrevaylerFactory.createTransientPrevayler((Serializable)prevalentSystem);
		Object result = prevayler.execute(query());
		assertEquals(0, ((Integer)result).intValue());
	}

	private static Query query() {
		return new Query() {
			public Object query(Object prevalentSystem, Date ignored) throws Exception {
				return new Integer(((List)prevalentSystem).size());
			}
		};
	}

	public void testTransactionWithQuery() throws Exception {
		List prevalentSystem = new LinkedList();
		Prevayler prevayler = PrevaylerFactory.createTransientPrevayler((Serializable)prevalentSystem);
		Object result = prevayler.execute(transactionWithQuery());
		assertEquals("abc", result);
		assertEquals("added element", prevalentSystem.get(0));
	}

	private static TransactionWithQuery transactionWithQuery() {
		return new TransactionWithQuery() {
			public Object executeAndQuery(Object prevalentSystem, Date timestamp) {
				((List)prevalentSystem).add("added element");
				return "abc";
			}
		};
	}

}
