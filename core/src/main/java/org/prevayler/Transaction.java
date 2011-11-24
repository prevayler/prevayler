//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler;

import java.io.Serializable;
import java.util.Date;

/** An atomic transaction to be executed on a Prevalent System. Any operation which changes the observable state of a Prevalent System must be encapsulated in a Transaction.
 * <br>
 * <br>IMPORTANT: Transaction objects CANNOT reference business objects directly. Instead, you must search for the business objects you need via the given <code>prevalentSystem</code>. See org.prevayler.demos for usage examples.
 * <br>
 * <br>Business objects referenced by a Transaction object will be mere copies of the original business objects when that Transaction object is recovered from the serialized journal file. This will make the transactions work when they are executed for the first time but have no effect during shutdown recovery. This is known as the prevalence baptism problem because everyone comes across it, despite this warning.
 * <br>
 * @param <P> The type or any supertype of the Prevalent System you intend to perform the transaction upon. <br>
 */

public interface Transaction<P> extends Serializable{

	/** This method is called by Prevayler.execute(Transaction) to execute this Transaction on the given Prevalent System. See org.prevayler.demos for usage examples.
	 * @param prevalentSystem The system on which this Transaction will execute.
	 * @param executionTime The time at which this Transaction is being executed. Every Transaction executes completely within a single moment in time. Logically, a Prevalent System's time does not pass during the execution of a Transaction.
	 */
	public void executeOn(P prevalentSystem, Date executionTime);

}
