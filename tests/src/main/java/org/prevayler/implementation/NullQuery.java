package org.prevayler.implementation;

import java.io.Serializable;
import java.util.Date;

import org.prevayler.Query;

public class NullQuery implements Query {

	public Object query(Serializable prevalentSystem, Date executionTime) throws Exception {
		return null;
	}

}
