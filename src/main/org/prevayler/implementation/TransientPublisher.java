// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import java.io.IOException;
import java.util.*;

import org.prevayler.Transaction;


/** A TransactionPublisher that DOES NOT persist the published Transactions. This class is used to run demos or application tests orders of magnitude faster than with the logging turned on. It is also extended by many other TransactionPublisher implementations.
 */
public abstract class TransientPublisher implements TransactionPublisher {

	private final Set _subscribers = new HashSet();


	public void addSubscriber(TransactionSubscriber subscriber, long initialTransactionIgnored) throws IOException, ClassNotFoundException {
		synchronized (_subscribers) { _subscribers.add(subscriber);	}
	}

	protected void notifySubscribers(Transaction transaction, Date timestamp) {
		synchronized (_subscribers) {
			Iterator i = _subscribers.iterator();
			while (i.hasNext()) ((TransactionSubscriber)i.next()).receive(transaction, timestamp);
		}
	}

}
