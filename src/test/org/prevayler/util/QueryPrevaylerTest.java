package org.prevayler.util;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.prevayler.Prevayler;
import org.prevayler.implementation.TransientPrevayler;
import org.prevayler.implementation.clock.BrokenClock;


public class QueryPrevaylerTest extends TestCase {

	public void testQuery() throws Exception {
		List prevalentSystem = new LinkedList();
		Prevayler prevayler = new TransientPrevayler(prevalentSystem, new BrokenClock());
		QueryPrevayler queryPrevayler = new QueryPrevayler(prevayler);
		Object result = queryPrevayler.performAlone(query());
		assertEquals(0, ((Integer)result).intValue());
	}

	private static Query query() {
		return new Query() {
			public Object performOn(Object prevalentSystem) throws Exception {
				return new Integer(((List)prevalentSystem).size());
			}
		};
	}

	public void testTransactionWithQuery() throws Exception {
		List prevalentSystem = new LinkedList();
		Prevayler prevayler = new TransientPrevayler(prevalentSystem, new BrokenClock());
		QueryPrevayler queryPrevayler = new QueryPrevayler(prevayler);
		Object result = queryPrevayler.execute(transactionWithQuery());
		assertEquals("result", result);
		assertEquals("added element", prevalentSystem.get(0));
	}

	private static TransactionWithQuery transactionWithQuery() {
		return new TransactionWithQuery() {
			public Object executeAndQuery(Object prevalentSystem, Date timestamp) {
				((List)prevalentSystem).add("added element");
				return "result";
			}
		};
	}

}
