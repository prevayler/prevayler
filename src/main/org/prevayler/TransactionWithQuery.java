//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler;

import java.io.Serializable;
import java.util.Date;

/** A Transaction that also returns a result or throws an Exception after executing. <br><br>A "PersonCreation" Transaction, for example, may return the Person it created. Without this, to retrieve the newly created Person, the caller would have to issue a Query like: "What was the last Person I created?". <br><br>Looking at the Prevayler demos is by far the best way to learn how to use this class.
 * @see Transaction
 */
public interface TransactionWithQuery extends Serializable {
	
	/** Performs the necessary modifications on the given prevalentSystem and also returns an Object or throws an Exception.
	 * This method is called by Prevayler.execute(TransactionWithQuery) to execute this TransactionWithQuery on the given Prevalent System. See org.prevayler.demos for usage examples.
	 * @param prevalentSystem The system on which this TransactionWithQuery will execute.
	 * @param executionTime The time at which this TransactionWithQuery is being executed. Every Transaction executes completely within a single moment in time. Logically, a Prevalent System's time does not pass during the execution of a Transaction.
	 */
	public Object executeAndQuery(Object prevalentSystem, Date executionTime) throws Exception;
	
}
