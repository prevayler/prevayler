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
public class POBox extends Thread implements TransactionSubscriber {

    private final LinkedList _queue = new LinkedList();

    private final TransactionSubscriber _delegate;

    private final Object _emptynessMonitor = new Object();

    public POBox(TransactionSubscriber delegate) {
        _delegate = delegate;
        setDaemon(true);
        start();
    }

    public synchronized void receive(TransactionTimestamp transactionTimestamp) {
        _queue.add(transactionTimestamp);
        notify();
    }

    public void run() {
        while (true) {
            TransactionTimestamp notification = waitForNotification();
            _delegate.receive(notification);
        }
    }

    private synchronized TransactionTimestamp waitForNotification() {
        while (_queue.size() == 0) {
            synchronized (_emptynessMonitor) {
                _emptynessMonitor.notify();
            }
            Cool.wait(this);
        }
        return (TransactionTimestamp) _queue.removeFirst();
    }

    public void waitToEmpty() {
        synchronized (_emptynessMonitor) {
            while (_queue.size() != 0)
                Cool.wait(_emptynessMonitor);
        }
    }

}
