//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler;

import java.io.Serializable;
import java.util.Date;

/** The same as TransactionWithQuery except it does not throw Exception when executed.
 * @see TransactionWithQuery
 */
public interface SureTransactionWithQuery<P extends Serializable,R> extends TransactionWithQuery<P,R>{

	/** The same as TransactionWithQuery.executeAndQuery(P, Date) except it does not throw Exception when executed.
	 * @see TransactionWithQuery#executeAndQuery(P, Date)
	 */
	public R executeAndQuery(P prevalentSystem, Date executionTime);

}
