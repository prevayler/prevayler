//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.publishing;

import org.prevayler.Clock;
import org.prevayler.TransactionBase;
import org.prevayler.implementation.Capsule;

import java.io.IOException;


public interface TransactionPublisher<P> {

  /**
   * Updates the given subscriber with all transactions published since initialTransaction, returns and continues publishing all future transactions to him.
   */
  public void subscribe(TransactionSubscriber<P> subscriber, long initialTransaction) throws IOException, ClassNotFoundException;

  /**
   * Stops publishing future transactions to the given subscriber.
   */
  public void cancelSubscription(TransactionSubscriber<P> subscriber);

  /**
   * Publishes transaction to the subscribers synchronously. This method will only return after all subscribers have received transaction. Note that no guarantee can be made as to wether the subscribers have actually executed it.
   */
  public void publish(Capsule<? super P, ? extends TransactionBase> capsule);

  /**
   * Returns a Clock which is consistent with the Transaction publishing time.
   */
  public Clock clock();

  /**
   * Closes any files or other system resources opened by this TransactionPublisher.
   */
  public void close() throws IOException;

}
