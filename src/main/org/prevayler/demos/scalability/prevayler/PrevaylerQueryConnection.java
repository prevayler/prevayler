package org.prevayler.demos.scalability.prevayler;

import org.prevayler.demos.scalability.*;
import java.util.List;

class PrevaylerQueryConnection implements QueryConnection {

	private final QuerySystem querySystem;


	PrevaylerQueryConnection(QuerySystem querySystem) {
		this.querySystem = querySystem;
	}


	public List queryByName(String name) {
		return querySystem.queryByName(name);
	}
}
