//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Frederic Langlet

package org.prevayler.implementation.publishing;

import org.prevayler.Clock;
import org.prevayler.Transaction;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/** This class provides basic subscriber addition and notification.
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

	public synchronized void removeSubscriber(TransactionSubscriber subscriber) {
		_subscribers.remove(subscriber);
	}

    protected synchronized void notifySubscribers(Transaction transaction, long systemVersion, Date executionTime) {
		Iterator i = _subscribers.iterator();
        while (i.hasNext()) ((TransactionSubscriber) i.next()).receive(transaction, systemVersion, executionTime);
    }

}
