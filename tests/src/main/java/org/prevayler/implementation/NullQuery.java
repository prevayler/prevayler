package org.prevayler.implementation;

import java.io.Serializable;
import java.util.Date;

import org.prevayler.Query;

public class NullQuery implements Query<Object,Object> {

	public Object query(Object prevalentSystem, Date executionTime) throws Exception {
		return null;
	}

}
