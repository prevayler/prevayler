//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.publishing;

import org.prevayler.Clock;
import org.prevayler.Transaction;
import org.prevayler.foundation.Cool;
import org.prevayler.foundation.Turn;
import org.prevayler.implementation.clock.PausableClock;
import org.prevayler.implementation.journal.Journal;
import org.prevayler.implementation.publishing.censorship.TransactionCensor;

import java.io.IOException;
import java.util.Date;

public class CentralPublisher extends AbstractPublisher {

	private final PausableClock _pausableClock;
	private final TransactionCensor _censor;
	private final Journal _journal;
	private long _nextTransaction;

	private volatile int _pendingPublications = 0;
	private final Object _pendingPublicationsMonitor = new Object();

	private Turn _nextTurn = Turn.first();
	private final Object _nextTurnMonitor = new Object();


	public CentralPublisher(Clock clock, TransactionCensor censor, Journal journal) {
		super(new PausableClock(clock));
		_pausableClock = (PausableClock)_clock; //This is just to avoid casting the inherited _clock every time.

		_censor = censor;
		_journal = journal;
	}


	public void publish(Transaction transaction) {
		synchronized (_pendingPublicationsMonitor) {  //Blocks all new subscriptions until the publication is over.
			if (_pendingPublications == 0) _pausableClock.pause();
			_pendingPublications++;
		}

		try {
			publishWithoutWorryingAboutNewSubscriptions(transaction);  // Suggestions for a better method name are welcome.  :)
		} finally {
			synchronized (_pendingPublicationsMonitor) {
				_pendingPublications--;
				if (_pendingPublications == 0) {
					_pausableClock.resume();
					_pendingPublicationsMonitor.notifyAll();
				}
			}
		}
	}


	private void publishWithoutWorryingAboutNewSubscriptions(Transaction transaction) {
		Turn myTurn = nextTurn();

		Date executionTime = realTime(myTurn);  //TODO realTime() and approve in the same turn.
		long systemVersion = approve(transaction, executionTime, myTurn);
		_journal.append(transaction, executionTime, myTurn);
		notifySubscribers(transaction, systemVersion, executionTime, myTurn);
	}


	private Turn nextTurn() {
		synchronized (_nextTurnMonitor) {
			Turn result = _nextTurn;
			_nextTurn = _nextTurn.next();
			return result;
		}
	}


	private Date realTime(Turn myTurn) {
		try {
			myTurn.start();
			return _pausableClock.realTime();
		} finally {	myTurn.end(); }
	}


	private long approve(Transaction transaction, Date executionTime, Turn myTurn) throws RuntimeException, Error {
		try {
			myTurn.start();

			_censor.approve(transaction, _nextTransaction, executionTime);

			long systemVersion = _nextTransaction++;

			myTurn.end();

			return systemVersion;
		} catch (RuntimeException r) { myTurn.alwaysSkip(); throw r;
		} catch (Error e) { myTurn.alwaysSkip(); throw e; }
	}


	private void notifySubscribers(Transaction transaction, long systemVersion, Date executionTime, Turn myTurn) {
		try {
			myTurn.start();
			_pausableClock.advanceTo(executionTime);
			notifySubscribers(transaction, systemVersion, executionTime);
		} finally {	myTurn.end(); }
	}


	public void addSubscriber(TransactionSubscriber subscriber, long initialTransaction) throws IOException, ClassNotFoundException {
		synchronized (_pendingPublicationsMonitor) {
			while (_pendingPublications != 0) Cool.wait(_pendingPublicationsMonitor);
			
			_journal.update(subscriber, initialTransaction);
			_nextTransaction = _journal.nextTransaction();

			super.addSubscriber(subscriber);
		}
	}


	public void close() throws IOException { _journal.close(); }

}
