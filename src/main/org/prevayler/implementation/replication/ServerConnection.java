//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.replication;

import org.prevayler.foundation.network.ObjectSocket;
import org.prevayler.implementation.Capsule;
import org.prevayler.implementation.TransactionCapsule;
import org.prevayler.implementation.TransactionTimestamp;
import org.prevayler.implementation.publishing.POBox;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.publishing.TransactionSubscriber;

import java.io.IOException;
import java.util.Date;


/** Reserved for future implementation.
 */
class ServerConnection extends Thread implements TransactionSubscriber {

	static final String SUBSCRIBER_UP_TO_DATE = "SubscriberUpToDate";
	static final String REMOTE_TRANSACTION = "RemoteTransaction";
	static final String CLOCK_TICK = "ClockTick";

	private final TransactionPublisher _publisher;
	private TransactionCapsule _remoteCapsule;

	private final ObjectSocket _remote;


	ServerConnection(TransactionPublisher publisher, ObjectSocket remoteSocket) throws IOException {
		_publisher = publisher;
		_remote = remoteSocket;
		setDaemon(true);
		start();
	}



	public void run() {
		try {		
			long initialTransaction = ((Long)_remote.readObject()).longValue();
			_publisher.subscribe(new POBox(this), initialTransaction);
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
						synchronized (_remote) {
							_remote.writeObject(CLOCK_TICK);
							_remote.writeObject(_publisher.clock().time());
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
		_remoteCapsule = (TransactionCapsule)_remote.readObject();
		try {
			_publisher.publish(_remoteCapsule);
		} catch (RuntimeException rx) {
			send(rx);
		} catch (Error error) {
			send(error);
		}
	}


	public void receive(TransactionTimestamp transactionTimestamp) {
		Capsule capsule = transactionTimestamp.capsule();
		long systemVersion = transactionTimestamp.systemVersion();
		Date executionTime = transactionTimestamp.executionTime();

		try {
			synchronized (_remote) {
				_remote.writeObject(capsule == _remoteCapsule
					? (Object)REMOTE_TRANSACTION
					: capsule
				);
				_remote.writeObject(executionTime);
				_remote.writeObject(new Long(systemVersion));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//TODO Cancel subscription.
		}
	}


	private void send(Object object) {
		synchronized (_remote) {
			try {
				_remote.writeObject(object);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
