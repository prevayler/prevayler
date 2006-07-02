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
public class POBox<S> extends Thread implements TransactionSubscriber<S> {

    private final LinkedList<TransactionTimestamp<S, ?, ?>> _queue = new LinkedList<TransactionTimestamp<S, ?, ?>>();

    private final TransactionSubscriber<S> _delegate;

    private final Object _emptynessMonitor = new Object();

    public POBox(TransactionSubscriber<S> delegate) {
        _delegate = delegate;
        setDaemon(true);
        start();
    }

    public synchronized <R, E extends Exception> void receive(TransactionTimestamp<S, R, E> transactionTimestamp) {
        _queue.add(transactionTimestamp);
        notify();
    }

    @Override public void run() {
        while (true) {
            TransactionTimestamp<S, ?, ?> notification = waitForNotification();
            _delegate.receive(notification);
        }
    }

    private synchronized TransactionTimestamp<S, ?, ?> waitForNotification() {
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
