// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation.replica;

import java.io.IOException;
import java.net.ServerSocket;
import org.prevayler.implementation.TransactionPublisher;


public class PublishingServer extends Thread {

	static final int DEFAULT_PORT = 8756;

	private final TransactionPublisher _publisher;
	private final ServerSocket _serverSocket;


	public PublishingServer(TransactionPublisher publisher) throws IOException {
		_serverSocket = new ServerSocket(DEFAULT_PORT);
		_publisher = publisher;
		setDaemon(true);
		start();
	} 


	public void run() {
		try {
			while (true) new RemoteConnection(_publisher, _serverSocket.accept());
		} catch (IOException iox) {
			iox.printStackTrace();
		}
	}	
}
