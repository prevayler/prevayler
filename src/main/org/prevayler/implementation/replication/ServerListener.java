// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation.replication;

import java.io.IOException;
import java.net.ServerSocket;

import org.prevayler.implementation.publishing.TransactionPublisher;


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
