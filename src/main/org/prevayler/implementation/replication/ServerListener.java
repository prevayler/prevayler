//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.replication;

import org.prevayler.implementation.publishing.TransactionPublisher;

import java.io.IOException;
import java.net.ServerSocket;


/** Reserved for future implementation.
 */
public class ServerListener extends Thread {

	private final TransactionPublisher _publisher;
	private final ServerSocket _serverSocket;


	public ServerListener(TransactionPublisher publisher, int port) throws IOException {
		_serverSocket = new ServerSocket(port);
		_publisher = publisher;
		setDaemon(true);
		start();
	} 


	public void run() {
		try {
			while (true) new ServerConnection(_publisher, _serverSocket.accept());
		} catch (IOException iox) {
			iox.printStackTrace();
		}
	}	
}
