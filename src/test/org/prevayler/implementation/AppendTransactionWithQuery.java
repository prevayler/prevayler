package org.prevayler.implementation;

import org.prevayler.TransactionWithQuery;

import java.util.Date;

public class AppendTransactionWithQuery implements TransactionWithQuery {

	public String toAdd;

	private AppendTransactionWithQuery() {
		// Skaringa requires a default constructor, but XStream does not.
	}

	public AppendTransactionWithQuery(String toAdd) {
		this.toAdd = toAdd;
	}

	public Object executeAndQuery(Object prevalentSystem, Date executionTime) throws Exception {
		StringBuffer system = (StringBuffer) prevalentSystem;
		system.append(toAdd);
		return system.toString();
	}

}
