//Prevayler(TM) - The Open-Source Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld.
//This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation.publishing;

import java.io.IOException;
import java.util.Date;

import org.prevayler.Clock;
import org.prevayler.Transaction;
import org.prevayler.implementation.clock.PausableClock;
import org.prevayler.implementation.logging.TransactionLogger;
import org.prevayler.implementation.publishing.censorship.*;

public class CentralPublisher extends AbstractPublisher {

	private final TransactionCensor _censor;
	private final TransactionLogger _logger;
	private final PausableClock _pausableClock;

	public CentralPublisher(Clock clock, TransactionCensor censor, TransactionLogger logger) {
		super(new PausableClock(clock));
		_pausableClock = (PausableClock)_clock; //This is just to avoid casting the inherited _clock every time.

		_censor = censor;
		_logger = logger;
	}

	public synchronized void publish(Transaction transaction) { //TODO Optimization: Remove this synchronization and create a transaction pipeline: timestamp, approve, log, notify. The clock must be adapted to reflect this change.
		_pausableClock.pause();
		try {
			Date executionTime = _pausableClock.time();
			_censor.approve(transaction, executionTime);  //Throws RuntimeException or Error if the transaction is bad.
			_logger.log(transaction, executionTime);
			notifySubscribers(transaction, executionTime);
		} finally {
			_pausableClock.resume();
		}
	}

	public synchronized void addSubscriber(TransactionSubscriber subscriber, long initialTransaction) throws IOException, ClassNotFoundException {
		_logger.update(subscriber, initialTransaction);
		super.addSubscriber(subscriber);
	}

}
