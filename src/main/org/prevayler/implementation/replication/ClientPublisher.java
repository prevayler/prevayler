//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.replication;

import org.prevayler.Clock;
import org.prevayler.foundation.network.OldNetwork;
import org.prevayler.foundation.network.ObjectSocket;
import org.prevayler.implementation.Capsule;
import org.prevayler.implementation.TransactionTimestamp;
import org.prevayler.implementation.clock.BrokenClock;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.publishing.TransactionSubscriber;

import java.io.IOException;
import java.util.Date;


/** Reserved for future implementation.
 */
public class ClientPublisher implements TransactionPublisher {

	private final BrokenClock _clock = new BrokenClock();

	private TransactionSubscriber _subscriber;
	private final Object _upToDateMonitor = new Object();

	private Capsule _myCapsule;
	private final Object _myCapsuleMonitor = new Object();
	private RuntimeException _myTransactionRuntimeException;
	private Error _myTransactionError;

	private final ObjectSocket _server;


	public ClientPublisher(OldNetwork network, String serverIpAddress, int serverPort) throws IOException {
		System.out.println("The replication logic is still under development.");
		_server = network.openSocket(serverIpAddress, serverPort);
		startListening();
	}


	private void startListening() {
		Thread listener = new Thread() {
			public void run() {
				try {
					while (true) receiveTransactionFromServer();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		listener.setDaemon(true);
		listener.start();
	}


	public synchronized void subscribe(TransactionSubscriber subscriber, long initialTransaction) throws IOException, ClassNotFoundException {
		if (_subscriber != null) throw new UnsupportedOperationException("The current implementation can only support one subscriber. Future implementations will support more.");
		_subscriber = subscriber;
		synchronized (_upToDateMonitor) {
			_server.writeObject(new Long(initialTransaction));
			wait(_upToDateMonitor);
		}
	}


	public void cancelSubscription(TransactionSubscriber subscriber) {
		throw new UnsupportedOperationException("Removing subscribers is not yet supported by the current implementation.");
	}


	//TODO Remove synchronized allowing multiple transactions to be sent at a time.
	public synchronized void publish(Capsule capsule) {  
		if (_subscriber == null) throw new IllegalStateException("To publish a transaction, this ClientPublisher needs a registered subscriber.");
		synchronized (_myCapsuleMonitor) {
			_myCapsule = capsule;
			
			try {
				_server.writeObject(capsule);
			} catch (IOException iox) {
				iox.printStackTrace();
				while (true) Thread.yield();  //Remove all exceptions when using StubbornNetwork.
			}
			wait(_myCapsuleMonitor);
			
			throwEventualErrors();
		}
	}


	private void throwEventualErrors() throws RuntimeException, Error {
		try {
			if (_myTransactionRuntimeException != null) throw _myTransactionRuntimeException;
			if (_myTransactionError != null) throw _myTransactionError;
		} finally {
			_myTransactionRuntimeException = null;
			_myTransactionError = null;
		}
	}


	private void receiveTransactionFromServer() throws IOException, ClassNotFoundException {
		Object transactionCandidate = _server.readObject();
		
		if (transactionCandidate.equals(ServerConnection.SUBSCRIBER_UP_TO_DATE)) {
			synchronized (_upToDateMonitor) { _upToDateMonitor.notify(); }
			return;
		}

		if (transactionCandidate instanceof Date) {
			Date clockTick = (Date)transactionCandidate;
			_clock.advanceTo(clockTick);
			 return;
		}
		
		if (transactionCandidate instanceof RuntimeException) {
			_myTransactionRuntimeException = (RuntimeException)transactionCandidate;
			notifyMyTransactionMonitor();
			return;
		}
		if (transactionCandidate instanceof Error) {
			_myTransactionError = (Error)transactionCandidate;
			notifyMyTransactionMonitor();
			return;
		}

		TransactionTimestamp transactionTimestamp = (TransactionTimestamp)transactionCandidate;
		Date timestamp = transactionTimestamp.executionTime();
		long systemVersion = transactionTimestamp.systemVersion();
		
		_clock.advanceTo(timestamp);
		
		if (transactionTimestamp.capsule() == null) {
			_subscriber.receive(new TransactionTimestamp(_myCapsule, systemVersion, timestamp));
			notifyMyTransactionMonitor();
			return;
		}

		_subscriber.receive(new TransactionTimestamp(transactionTimestamp.capsule(), systemVersion, timestamp));
	}


	private static void wait(Object monitor) {
		try {
			monitor.wait();
		} catch (InterruptedException ix) {
			throw new RuntimeException("Unexpected InterruptedException.");
		}
	}


	private void notifyMyTransactionMonitor() {
		synchronized (_myCapsuleMonitor) {
			_myCapsuleMonitor.notify();
		}
	}


	public Clock clock() {
		return _clock;
	}


	public void close() throws IOException {
		_server.close();
	}

}
