// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation.replication;

import java.io.*;
import java.net.Socket;
import java.util.Date;

import org.prevayler.Transaction;
import org.prevayler.implementation.publishing.*;


/** Reserved for future implementation.
 */
class ServerConnection extends Thread implements TransactionSubscriber {

	static final String SUBSCRIBER_UP_TO_DATE = "SubscriberUpToDate";
	static final String REMOTE_TRANSACTION = "RemoteTransaction";
	static final String CLOCK_TICK = "ClockTick";

	private final TransactionPublisher _publisher;
	private Transaction _remoteTransaction;

	private final ObjectOutputStream _toRemote;
	private final ObjectInputStream _fromRemote;


	ServerConnection(TransactionPublisher publisher, Socket remoteSocket) throws IOException {
		_publisher = publisher;
		_fromRemote = new ObjectInputStream(remoteSocket.getInputStream());
		_toRemote = new ObjectOutputStream(remoteSocket.getOutputStream());
		setDaemon(true);
		start();
	}


	public void run() {
		try {		
			long initialTransaction = ((Long)_fromRemote.readObject()).longValue();
			_publisher.addSubscriber(this, initialTransaction);
			send(SUBSCRIBER_UP_TO_DATE);
			
			sendClockTicks();
			while (true) publishRemoteTransaction();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	private void sendClockTicks() {
		Thread clockTickSender = new Thread() {
			public void run() {
				try {
					while (true) {
						synchronized (_toRemote) {
							_toRemote.writeObject(CLOCK_TICK);
							_toRemote.writeObject(_publisher.clock().time());
						}
						Thread.sleep(1000);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		clockTickSender.setDaemon(true);
		clockTickSender.start();
	}


	void publishRemoteTransaction() throws Exception {
		_remoteTransaction = (Transaction)_fromRemote.readObject();
		try {
			_publisher.publish(_remoteTransaction);
		} catch (RuntimeException rx) {
			send(rx);
		} catch (Error error) {
			send(error);
		}
	}


	public void receive(Transaction transaction, Date timestamp) {
		try {
			synchronized (_toRemote) {
				_toRemote.writeObject(transaction == _remoteTransaction
					? (Object)REMOTE_TRANSACTION
					: transaction
				);
				_toRemote.writeObject(timestamp);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	private void send(Object object) {
		synchronized (_toRemote) {
			try {
				_toRemote.writeObject(object);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
