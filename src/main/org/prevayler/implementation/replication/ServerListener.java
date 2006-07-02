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

import org.prevayler.foundation.network.ObjectServerSocket;
import org.prevayler.implementation.publishing.TransactionPublisher;

import java.io.IOException;

public class ServerListener<S> extends Thread {

    private final TransactionPublisher<S> _publisher;

    private final ObjectServerSocket _serverSocket;

    // TODO Close the socket when the publisher is closed (listen for it or have
    // the Dashboard (new idea) close this when it closes the publisher).

    public ServerListener(TransactionPublisher<S> publisher, int port) throws IOException {
        _serverSocket = new ObjectServerSocket(port);
        _publisher = publisher;
        setDaemon(true);
        start();
        // FIXME: Make sure this thread ends when Prevayler is closed.
    }

    @Override public void run() {
        try {
            while (true) {
                new ServerConnection<S>(_publisher, _serverSocket.accept());
            }
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }

}
