//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Alexandre Nodari.

package org.prevayler.foundation.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.prevayler.foundation.Cool;


public class NetworkMock implements Network {

	private final Map _serverSocketByPort = new HashMap();
	
	public synchronized ObjectSocket openSocket(String serverIpAddress, int serverPort) throws IOException {
		if (!serverIpAddress.equals("localhost")) throw new IllegalArgumentException("Only localhost connections are supported by the NetworkMock.");
		
		ObjectServerSocketMock server = server(serverPort); 
		if (server == null) throw new IOException("No server is listening on this port.");
		try {
			return server.openClientSocket();
		} catch (IOException e) {
			Cool.sleep(5);
			return server.openClientSocket(); //TODO Eliminate this retry because client must try and reconnect anyway.
		}
	}

	public synchronized ObjectServerSocket openObjectServerSocket(int serverPort) throws IOException {
		ObjectServerSocketMock old = server(serverPort);
		if (old != null) throw new IOException("Port already in use.");

		ObjectServerSocketMock result = new ObjectServerSocketMock();
		_serverSocketByPort.put(new Integer(serverPort), result);
		
		return result;
	}

	private ObjectServerSocketMock server(int serverPort) {
		return (ObjectServerSocketMock) _serverSocketByPort.get(new Integer(serverPort));
	}
}
