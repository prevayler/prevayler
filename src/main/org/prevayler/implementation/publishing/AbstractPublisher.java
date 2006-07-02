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
public abstract class AbstractPublisher<S> implements TransactionPublisher<S> {

    protected final Clock _clock;

    private final List<TransactionSubscriber<S>> _subscribers = new LinkedList<TransactionSubscriber<S>>();

    public AbstractPublisher(Clock clock) {
        _clock = clock;
    }

    public Clock clock() {
        return _clock;
    }

    public synchronized void addSubscriber(TransactionSubscriber<S> subscriber) {
        _subscribers.add(subscriber);
    }

    public synchronized void cancelSubscription(TransactionSubscriber<S> subscriber) {
        _subscribers.remove(subscriber);
    }

    protected synchronized <R, E extends Exception> void notifySubscribers(TransactionTimestamp<S, R, E> transactionTimestamp) {
        Iterator<TransactionSubscriber<S>> i = _subscribers.iterator();
        while (i.hasNext())
            i.next().receive(transactionTimestamp);
    }

}
