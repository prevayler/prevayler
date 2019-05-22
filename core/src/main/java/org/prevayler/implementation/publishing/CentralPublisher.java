//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.publishing;

import org.prevayler.Clock;
import org.prevayler.TransactionBase;
import org.prevayler.foundation.Cool;
import org.prevayler.foundation.Turn;
import org.prevayler.implementation.Capsule;
import org.prevayler.implementation.TransactionGuide;
import org.prevayler.implementation.TransactionTimestamp;
import org.prevayler.implementation.clock.PausableClock;
import org.prevayler.implementation.journal.Journal;

import java.io.IOException;

public class CentralPublisher<P> extends AbstractPublisher<P, PausableClock> {

  private final Journal<P> _journal;

  private volatile int _pendingPublications = 0;
  private final Object _pendingPublicationsMonitor = new Object();

  private Turn _nextTurn = Turn.first();
  private long _nextTransaction;
  private final Object _nextTurnMonitor = new Object();


  public CentralPublisher(Clock clock, Journal<P> journal) {
    super(new PausableClock(clock));
    _journal = journal;
  }


  public void publish(Capsule<? super P, ? extends TransactionBase> capsule) {
    synchronized (_pendingPublicationsMonitor) {  //Blocks all new subscriptions until the publication is over.
      if (_pendingPublications == 0) _clock.pause();
      _pendingPublications++;
    }

    try {
      publishWithoutWorryingAboutNewSubscriptions(capsule);  // Suggestions for a better method name are welcome.  :)
    } finally {
      synchronized (_pendingPublicationsMonitor) {
        _pendingPublications--;
        if (_pendingPublications == 0) {
          _clock.resume();
          _pendingPublicationsMonitor.notifyAll();
        }
      }
    }
  }


  private void publishWithoutWorryingAboutNewSubscriptions(Capsule<? super P, ? extends TransactionBase> capsule) {
    TransactionGuide<? super P> guide = guideFor(capsule);
    _journal.append(guide);
    notifySubscribers(guide);
  }

  private TransactionGuide<? super P> guideFor(Capsule<? super P, ? extends TransactionBase> capsule) {
    synchronized (_nextTurnMonitor) {
      TransactionTimestamp<P> timestamp = new TransactionTimestamp<P>(capsule, _nextTransaction, _clock.realTime());

      // Count this transaction
      Turn turn = _nextTurn;
      _nextTurn = _nextTurn.next();
      _nextTransaction++;

      return new TransactionGuide<P>(timestamp, turn);
    }
  }

  private void notifySubscribers(TransactionGuide<? super P> guide) {
    guide.startTurn();
    try {
      _clock.advanceTo(guide.executionTime());
      notifySubscribers(guide.timestamp());
    } finally {
      guide.endTurn();
    }
  }


  public void subscribe(TransactionSubscriber<P> subscriber, long initialTransaction) throws IOException, ClassNotFoundException {
    synchronized (_pendingPublicationsMonitor) {
      while (_pendingPublications != 0) Cool.wait(_pendingPublicationsMonitor);

      _journal.update(subscriber, initialTransaction);

      synchronized (_nextTurnMonitor) {
        _nextTransaction = _journal.nextTransaction();
      }

      super.addSubscriber(subscriber);
    }
  }


  public void close() throws IOException {
    _journal.close();
  }

}
