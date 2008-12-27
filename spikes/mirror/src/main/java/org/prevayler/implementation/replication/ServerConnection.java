//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.replication;

import java.io.IOException;

import org.prevayler.foundation.network.ObjectSocket;
import org.prevayler.implementation.Capsule;
import org.prevayler.implementation.TransactionTimestamp;
import org.prevayler.implementation.publishing.POBox;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.publishing.TransactionSubscriber;


/** Reserved for future implementation.
 */
class ServerConnection extends Thread implements TransactionSubscriber {

	static final String SUBSCRIBER_UP_TO_DATE = "SubscriberUpToDate";
	static final String REMOTE_TRANSACTION = "RemoteTransaction";

	private final TransactionPublisher _publisher;
	private Capsule _remoteCapsule;

	private final ObjectSocket _remote;
	private final Thread _clockTickSender = createClockTickSender();

	ServerConnection(TransactionPublisher publisher, ObjectSocket remoteSocket) throws IOException {
		_publisher = publisher;
		_remote = remoteSocket;
		setDaemon(true);
		start();
	}



	public void run() {
		try {		
			long initialTransaction = ((Long)_remote.readObject()).longValue();
			
			POBox poBox = new POBox(this);
			_publisher.subscribe(poBox, initialTransaction);
			poBox.waitToEmpty();
			
			send(SUBSCRIBER_UP_TO_DATE);
			
			startSendingClockTicks();
			while (true) publishRemoteTransaction();
		} catch (IOException ex) {
			close();
		} catch (ClassNotFoundException ex) {
			close();
		}
	}


	private void startSendingClockTicks() {
		_clockTickSender.setDaemon(true);
		_clockTickSender.start();
	}


	private Thread createClockTickSender() {
		return new Thread() { //TODO Consider using TimerTask.
					public void run() {
						try {
							while (true) {
								synchronized (_remote) {
									_remote.writeObject(_publisher.clock().time());
								}
								Thread.sleep(1000);
							}
						} catch (InterruptedException ix) {
						} catch (IOException iox) {
							close();
						}
					}
				};
	}



	void publishRemoteTransaction() throws IOException, ClassNotFoundException {
		_remoteCapsule = (Capsule)_remote.readObject();
		try {
			_publisher.publish(_remoteCapsule);
		} catch (RuntimeException rx) {
			send(rx);
		} catch (Error error) {
			send(error);
		}
	}


	public void receive(TransactionTimestamp tt) {
		
		if (tt.capsule() == _remoteCapsule)
			tt = new TransactionTimestamp(null, tt.systemVersion(), tt.executionTime()); //TODO This is really ugly. It is using a null capsule inside the TransactionTimestamp to signal that the remote Capsule should be executed.
		
		try {
			synchronized (_remote) {
				_remote.writeObject(tt);
			}
		} catch (IOException ex) {
			close();
		}
	}


	private synchronized void close() {
		_clockTickSender.interrupt();
		this.interrupt();
		_publisher.cancelSubscription(this);
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
