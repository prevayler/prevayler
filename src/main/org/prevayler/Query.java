//Prevayler(TM) - The Open-Source Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld.
//This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

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
