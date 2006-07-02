// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation.replication;

import org.prevayler.foundation.network.ObjectSocket;
import org.prevayler.implementation.TransactionCapsule;
import org.prevayler.implementation.TransactionTimestamp;
import org.prevayler.implementation.publishing.POBox;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.publishing.TransactionSubscriber;

import java.io.IOException;

/**
 * Reserved for future implementation.
 */
class ServerConnection<S> extends Thread implements TransactionSubscriber<S> {

    static final String SUBSCRIBER_UP_TO_DATE = "SubscriberUpToDate";

    static final String REMOTE_TRANSACTION = "RemoteTransaction";

    private final TransactionPublisher<S> _publisher;

    private TransactionCapsule<S, ?, ?> _remoteCapsule;

    private final ObjectSocket _remote;

    private final Thread _clockTickSender = createClockTickSender();

    ServerConnection(TransactionPublisher<S> publisher, ObjectSocket remoteSocket) {
        _publisher = publisher;
        _remote = remoteSocket;
        setDaemon(true);
        start();
    }

    @Override public void run() {
        try {
            long initialTransaction = ((Long) _remote.readObject()).longValue();

            POBox<S> poBox = new POBox<S>(this);
            _publisher.subscribe(poBox, initialTransaction);
            poBox.waitToEmpty();

            send(SUBSCRIBER_UP_TO_DATE);

            startSendingClockTicks();

            while (true) {
                publishRemoteTransaction();
            }
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
        // TODO Consider using TimerTask.
        return new Thread() {
            @Override public void run() {
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

    @SuppressWarnings("unchecked") void publishRemoteTransaction() throws IOException, ClassNotFoundException {
        _remoteCapsule = (TransactionCapsule) _remote.readObject();
        try {
            _publisher.publish(_remoteCapsule);
        } catch (RuntimeException rx) {
            send(rx);
        } catch (Error error) {
            send(error);
        }
    }

    public <R, E extends Exception> void receive(TransactionTimestamp<S, R, E> tt) {
        if (tt.capsule() == _remoteCapsule) {
            // TODO This is really ugly. It is using a null capsule inside the
            // TransactionTimestamp to signal that the remote Capsule should be
            // executed.
            tt = new TransactionTimestamp<S, R, E>(null, tt.systemVersion(), tt.executionTime());
        }
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
