// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation.publishing;

import java.io.IOException;
import java.util.*;

import org.prevayler.Clock;
import org.prevayler.Transaction;


/** This class provides basic subscriber addition and notification.
 */
public abstract class AbstractPublisher implements TransactionPublisher {

	protected final Clock _clock;
	private final Set _subscribers = new HashSet();

	public AbstractPublisher(Clock clock) {
		_clock = clock;
	}

	public Clock clock() {
		return _clock;
	}

	public synchronized void addSubscriber(TransactionSubscriber subscriber) throws IOException, ClassNotFoundException {
		 _subscribers.add(subscriber);
	}

	protected synchronized void notifySubscribers(Transaction transaction, Date timestamp) {
		Iterator i = _subscribers.iterator();
		while (i.hasNext()) ((TransactionSubscriber)i.next()).receive(transaction, timestamp);
	}

}
