// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation.replica;

import java.io.*;
import java.net.Socket;
import org.prevayler.Transaction;
import org.prevayler.implementation.*;


public class RemotePublisher extends Thread implements TransactionPublisher {

	private TransactionSubscriber _subscriber;
	private final Object _subscriberUpToDateMonitor = new Object();

	private Transaction _myTransaction;
	private final Object _myTransactionMonitor = new Object();

	private final ObjectOutputStream _toServer;
	private final ObjectInputStream _fromServer;


	public RemotePublisher(String serverIpAddress) throws IOException, ClassNotFoundException {
		this(serverIpAddress, Protocol.DEFAULT_PORT);
	}


	public RemotePublisher(String serverIpAddress, int serverPort) throws IOException, ClassNotFoundException {
		Socket socket = new Socket(serverIpAddress, serverPort);
		_toServer = new ObjectOutputStream(socket.getOutputStream());   // Get the OUTPUT stream first. JDK 1.3.1_01 will lock up if you get the INPUT stream first.
		_fromServer = new ObjectInputStream(socket.getInputStream());
		setDaemon(true);
		start();
	}


	public synchronized void addSubscriber(TransactionSubscriber subscriber, long initialTransaction) throws IOException, ClassNotFoundException {
		if (_subscriber != null) throw new UnsupportedOperationException("The current implementation of RemoteTransactionPublisher can only support one subscriber. Future implementations will support more.");
		_subscriber = subscriber;
		synchronized (_subscriberUpToDateMonitor) {
			_toServer.writeObject(new Long(initialTransaction));
			wait(_subscriberUpToDateMonitor);
		}
	}


	public synchronized void publish(Transaction transaction) {
		if (_subscriber == null) throw new IllegalStateException("To publish a transaction, the RemoteTransactionPublisher needs a registered subscriber.");
		synchronized (_myTransactionMonitor) {
			_myTransaction = transaction;
			try {
				_toServer.writeObject(transaction);
			} catch (IOException iox) {
				iox.printStackTrace();
				while (true) Thread.yield();
			}
			wait(_myTransactionMonitor);
		}
	}


	public void run() {
		try {
			while (true) receiveTransactionFromServer();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	private void receiveTransactionFromServer() throws IOException, ClassNotFoundException {
		Object transactionCandidate = _fromServer.readObject();

		if (transactionCandidate.equals(Protocol.TRANSACTIONS_UP_TO_DATE)) {
			notify(_subscriberUpToDateMonitor);
			return;
		}

		if (transactionCandidate.equals(Protocol.REMOTE_TRANSACTION)) {
			synchronized (_myTransactionMonitor) {
				_subscriber.receive(_myTransaction);
				notify(_myTransactionMonitor);
			}
			return;
		}

		_subscriber.receive((Transaction)transactionCandidate);
	}


	private static void wait(Object monitor) {
		try {
			monitor.wait();
		} catch (InterruptedException ix) {
			throw new RuntimeException("Unexpected InterruptedException.");
		}
	}


	private static void notify(Object monitor) {
		synchronized (monitor) { monitor.notify(); }
	}

}
