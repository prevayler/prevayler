//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Frederic Langlet

package org.prevayler.implementation.publishing;

import org.prevayler.foundation.Cool;
import org.prevayler.implementation.TransactionTimestamp;

import java.util.LinkedList;


/** An assyncronous buffer for transaction subscribers. 
 */
public class POBox extends Thread implements TransactionSubscriber {
	
	private final LinkedList _queue = new LinkedList();
	private final TransactionSubscriber _delegate;
  

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
		while (_queue.size() == 0) Cool.wait(this);
		return (TransactionTimestamp)_queue.removeFirst();
	}


}