//Prevayler(TM) - The Open-Source Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld.
//This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation.logging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.prevayler.Transaction;
import org.prevayler.implementation.publishing.TransactionSubscriber;


public class TransientLogger implements TransactionLogger {

	private final List log = new ArrayList();

	public synchronized void log(Transaction transaction, Date executionTime) {
		log.add(new TransactionLogEntry(transaction, executionTime));
	}

	public synchronized void update(TransactionSubscriber subscriber, long initialTransaction) throws IOException {
		int i = (int)initialTransaction - 1;  //Lists are zero based.
		if (i > log.size()) throw new IOException("Unable to find transactions from " + (log.size() + 1) + " to " + i + ".");

		while (i != log.size()) {
			TransactionLogEntry entry = (TransactionLogEntry)log.get(i);
			subscriber.receive(entry.transaction, entry.timestamp);
			i++;
		}
	}

}
