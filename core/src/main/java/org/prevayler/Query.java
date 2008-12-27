//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler;

import java.util.Date;

/** Represents a query that can be executed on a Prevalent System.
 * @see org.prevayler.Prevayler#execute(Query)
 */
public interface Query {
	
	/**
	 * @param prevalentSystem The Prevalent System to be queried.
	 * @param executionTime The "current" time.
	 * @return The result of this Query.
	 * @throws Exception Any Exception encountered by this Query.
	 */
	public Object query(Object prevalentSystem, Date executionTime) throws Exception;

}
