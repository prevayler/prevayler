//Prevayler(TM) - The Open-Source Prevalence Layer.
//Copyright (C) 2001 Klaus Wuestefeld.
//This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

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
