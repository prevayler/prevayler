//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Frederic Langlet

package org.prevayler.implementation.publishing;

import org.prevayler.Transaction;
import org.prevayler.implementation.TransactionTimestamp;

import java.util.Date;
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


	public synchronized void receive(Transaction transaction, long systemVersion, Date executionTime) {
		_queue.add(new TransactionTimestamp(transaction, systemVersion, executionTime));
		notify();
	}


	public void run() {
		while (true) {
			TransactionTimestamp notification = waitForNotification();
			_delegate.receive(notification.transaction(), notification.systemVersion(), notification.timestamp());
		}
	}


	private synchronized TransactionTimestamp waitForNotification() {
		while (_queue.size() == 0) waitWithoutInterruptions();
		return (TransactionTimestamp)_queue.removeFirst();
	}


	private void waitWithoutInterruptions() {
		try {
			wait();
		} catch (InterruptedException e) {
			throw new RuntimeException("Unexpected InterruptedException.");
		}
	}

}