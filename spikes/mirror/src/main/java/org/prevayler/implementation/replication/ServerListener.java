//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.replication;

import org.prevayler.foundation.network.ObjectServerSocket;
import org.prevayler.foundation.network.OldNetworkImpl;
import org.prevayler.implementation.publishing.TransactionPublisher;

import java.io.IOException;


/**
 * Reserved for future implementation.
 */
public class ServerListener<P> extends Thread {

  private final TransactionPublisher<P> _publisher;
  private final ObjectServerSocket _serverSocket;

  //TODO Close the socket when the publisher is closed (listen for it or have the Dashboard (new idea) close this when it closes the publisher).

  public ServerListener(TransactionPublisher<P> publisher, OldNetworkImpl network, int port) throws IOException {
    _serverSocket = network.openObjectServerSocket(port);
    _publisher = publisher;
    setDaemon(true);
    start(); //FIXME: Make sure this thread ends when Prevayler is closed.
  }

  public void run() {
    try {
      while (true) new ServerConnection<P>(_publisher, _serverSocket.accept());
    } catch (IOException iox) {
      iox.printStackTrace();
    }
  }
}
