// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation.publishing;

import org.prevayler.Clock;
import org.prevayler.implementation.TransactionCapsule;

public interface TransactionPublisher<S> {

    /**
     * Updates the given subscriber with all transactions published since
     * initialTransaction, returns and continues publishing all future
     * transactions to him.
     */
    public void subscribe(TransactionSubscriber<S> subscriber, long initialTransaction);

    /**
     * Stops publishing future transactions to the given subscriber.
     */
    public void cancelSubscription(TransactionSubscriber<S> subscriber);

    /**
     * Publishes transaction to the subscribers synchronously. This method will
     * only return after all subscribers have received transaction. Note that no
     * guarantee can be made as to wether the subscribers have actually executed
     * it.
     */
    public <R, E extends Exception> void publish(TransactionCapsule<S, R, E> capsule);

    /**
     * Returns a Clock which is consistent with the Transaction publishing time.
     */
    public Clock clock();

    /**
     * Closes any files or other system resources opened by this
     * TransactionPublisher.
     */
    public void close();

}
