//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler;

import java.util.Date;

/**
 * An atomic transaction to be executed on a Prevalent System.
 * <br>
 * <br>To be recoverable, any changes to the observable state of a Prevalent System must be encapsulated in Transactions and performed via the given <code>prevalentSystem</code> in each Transaction.
 * <br>
 * <br>Upon recovery execution, anything outside <code>prevalentSystem</code> will be a freshly deserialized copy, so cannot reference anything in the Prevalent System.
 * <br>
 *
 * @param <P> The type or any supertype of the Prevalent System you intend to perform the transaction upon. <br>
 */

public interface Transaction<P> extends TransactionBase {

  /**
   * This method is called by Prevayler.execute(Transaction) to execute this Transaction on the given Prevalent System. See org.prevayler.demos for usage examples.
   *
   * @param prevalentSystem The system on which this Transaction will execute.
   * @param executionTime   The time at which this Transaction is being executed. Every Transaction executes completely within a single moment in time. Logically, a Prevalent System's time does not pass during the execution of a Transaction.
   */
  public void executeOn(P prevalentSystem, Date executionTime);

}
