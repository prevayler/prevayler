package org.prevayler.implementation;

import java.util.Date;

import org.prevayler.Query;

public class NullQuery implements Query {

	public Object query(Object prevalentSystem, Date executionTime) throws Exception {
		return null;
	}

}
