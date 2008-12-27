//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler;

import java.util.Date;

/** The same as TransactionWithQuery except it does not throw Exception when executed.
 * @see TransactionWithQuery
 */
public interface SureTransactionWithQuery extends TransactionWithQuery {

	/** The same as TransactionWithQuery.execute(Object, Date) except it does not throw Exception when executed.
	 * @see TransactionWithQuery#executeAndQuery(Object, Date)
	 */
	public Object executeAndQuery(Object prevalentSystem, Date executionTime);

}
