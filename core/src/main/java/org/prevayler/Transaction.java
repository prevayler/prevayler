//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler;

import java.io.Serializable;
import java.util.Date;

/** An atomic Transaction to be executed on a Prevalent System.
 * <br>
 * <br>To be recoverable, <b>any changes to the observable state of a Prevalent System must be encapsulated in Transactions and performed via</b> <code>prevalentSystem</code>.
 * <br>
 * <br>When <code>executeOn</code> is called upon recovery after a Transaction has been deserialized, anything in <code>prevalentSystem</code> is guaranteed to be reference-<i>inequal</i> against anything outside it. That's because everything outside it is a deserialized copy. So changes to <code>prevalentSystem</code> <b>must also never depend on reference equality between anything in it and anything outside it</b>.
 * <br>
 * <br>The 2 rules in bold above can be boiled down to "<b>stick to the</b> <code>prevalentSystem</code>" and guarantees full recoverability in Prevayler.
 * @param <P> The type of object you intend to perform the transaction on.
 */

public interface Transaction<P> extends Serializable{

	/** This method is called by Prevayler.execute(Transaction) to execute this Transaction on the given Prevalent System. See org.prevayler.demos for usage examples.
	 * @param prevalentSystem The system on which this Transaction will execute.
	 * @param executionTime The time at which this Transaction is being executed. Every Transaction executes completely within a single moment in time. Logically, a Prevalent System's time does not pass during the execution of a Transaction.
	 */
	public void executeOn(P prevalentSystem, Date executionTime);

}
