// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation.publishing;

import org.prevayler.Clock;
import org.prevayler.Transaction;

import java.io.IOException;


public interface TransactionPublisher {

	/** Updates the subscriber with all transactions published since initialTransaction, returns and continues publishing all future transactions to the subscriber.
	 */
	public void addSubscriber(TransactionSubscriber subscriber, long initialTransaction) throws IOException, ClassNotFoundException;

	/** Publishes transaction to the subscribers synchronously. This method will only return after all subscribers have received transaction. Note that no guarantee can be made as to wether the subscribers have actually executed it.
	 */
	public void publish(Transaction transaction);

	/** Returns a Clock which is consistent with the Transaction publishing time.
	 */
	public Clock clock();

}
