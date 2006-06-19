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
import org.prevayler.implementation.TransactionTimestamp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class provides basic subscriber addition and notification.
 */
public abstract class AbstractPublisher implements TransactionPublisher {

    protected final Clock _clock;

    private final List _subscribers = new LinkedList();

    public AbstractPublisher(Clock clock) {
        _clock = clock;
    }

    public Clock clock() {
        return _clock;
    }

    public synchronized void addSubscriber(TransactionSubscriber subscriber) {
        _subscribers.add(subscriber);
    }

    public synchronized void cancelSubscription(TransactionSubscriber subscriber) {
        _subscribers.remove(subscriber);
    }

    protected synchronized void notifySubscribers(TransactionTimestamp transactionTimestamp) {
        Iterator i = _subscribers.iterator();
        while (i.hasNext())
            ((TransactionSubscriber) i.next()).receive(transactionTimestamp);
    }

}
