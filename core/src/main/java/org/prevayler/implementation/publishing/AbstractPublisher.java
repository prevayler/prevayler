//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Frederic Langlet

package org.prevayler.implementation.publishing;

import org.prevayler.Clock;
import org.prevayler.implementation.TransactionTimestamp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * This class provides basic subscriber addition and notification.
 */
public abstract class AbstractPublisher<P, C extends Clock> implements TransactionPublisher<P> {

  protected final C _clock;
  private final List<TransactionSubscriber<P>> _subscribers = new LinkedList<TransactionSubscriber<P>>();


  public AbstractPublisher(C clock) {
    _clock = clock;
  }

  public Clock clock() {
    return _clock;
  }

  public synchronized void addSubscriber(TransactionSubscriber<P> subscriber) {
    _subscribers.add(subscriber);
  }

  public synchronized void cancelSubscription(TransactionSubscriber<P> subscriber) {
    _subscribers.remove(subscriber);
  }

  protected synchronized void notifySubscribers(TransactionTimestamp<? super P> transactionTimestamp) {
    Iterator<TransactionSubscriber<P>> i = _subscribers.iterator();
    while (i.hasNext()) i.next().receive(transactionTimestamp);
  }

}
