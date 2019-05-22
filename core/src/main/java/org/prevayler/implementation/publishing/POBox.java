//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Frederic Langlet

package org.prevayler.implementation.publishing;

import org.prevayler.foundation.Cool;
import org.prevayler.implementation.TransactionTimestamp;

import java.util.LinkedList;


/**
 * An assyncronous buffer for transaction subscribers.
 */
public class POBox<P> implements TransactionSubscriber<P>, Runnable {

  private final LinkedList<TransactionTimestamp<? super P>> _queue = new LinkedList<TransactionTimestamp<? super P>>();
  private final TransactionSubscriber<P> _delegate;

  private final Object _emptynessMonitor = new Object();


  public POBox(TransactionSubscriber<P> delegate) {
    _delegate = delegate;
    Cool.startDaemon(this);
  }


  public synchronized void receive(TransactionTimestamp<? super P> transactionTimestamp) {
    _queue.add(transactionTimestamp);
    notify();
  }


  public void run() {
    while (true) {
      TransactionTimestamp<? super P> notification = waitForNotification();
      _delegate.receive(notification);
    }
  }


  private synchronized TransactionTimestamp<? super P> waitForNotification() {
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
      while (_queue.size() != 0) Cool.wait(_emptynessMonitor);
    }
  }


}