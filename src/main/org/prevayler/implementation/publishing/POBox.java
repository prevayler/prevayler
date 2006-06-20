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

import org.prevayler.foundation.Cool;
import org.prevayler.implementation.TransactionTimestamp;

import java.util.LinkedList;

/**
 * An asyncronous buffer for transaction subscribers.
 */
public class POBox<T> extends Thread implements TransactionSubscriber<T> {

    private final LinkedList<TransactionTimestamp<?, T>> _queue = new LinkedList<TransactionTimestamp<?, T>>();

    private final TransactionSubscriber<T> _delegate;

    private final Object _emptynessMonitor = new Object();

    public POBox(TransactionSubscriber<T> delegate) {
        _delegate = delegate;
        setDaemon(true);
        start();
    }

    public synchronized <X> void receive(TransactionTimestamp<X, T> transactionTimestamp) {
        _queue.add(transactionTimestamp);
        notify();
    }

    @Override public void run() {
        while (true) {
            TransactionTimestamp<?, T> notification = waitForNotification();
            _delegate.receive(notification);
        }
    }

    private synchronized TransactionTimestamp<?, T> waitForNotification() {
        while (_queue.size() == 0) {
            synchronized (_emptynessMonitor) {
                _emptynessMonitor.notify();
            }
            Cool.wait(this);
        }
        return _queue.removeFirst();
    }

    public void waitToEmpty() {
        synchronized (_emptynessMonitor) {
            while (_queue.size() != 0)
                Cool.wait(_emptynessMonitor);
        }
    }

}
