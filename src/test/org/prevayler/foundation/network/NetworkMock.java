//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Alexandre Nodari.

package org.prevayler.foundation.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class NetworkMock implements OldNetwork {

	private final Map _serverSocketByPort = new HashMap();
	private Permit _permit = new Permit();
	
	public synchronized ObjectSocket openSocket(String serverIpAddress, int serverPort) throws IOException {
		if (!serverIpAddress.equals("localhost")) throw new IllegalArgumentException("Only localhost connections are supported by the NetworkMock.");
		
		ObjectServerSocketMock server = server(serverPort); 
		if (server == null) throw new IOException("No server is listening on this port.");
		return server.openClientSocket();
	}

	public synchronized ObjectServerSocket openObjectServerSocket(int serverPort) throws IOException {
		ObjectServerSocketMock old = server(serverPort);
		if (old != null) throw new IOException("Port already in use.");

		ObjectServerSocketMock result = new ObjectServerSocketMock(_permit);
		_serverSocketByPort.put(new Integer(serverPort), result);
		
		return result;
	}

	public void crash() {
		_permit.expire();
	}

	public void recover() {
		_permit = new Permit();
	}

	private ObjectServerSocketMock server(int serverPort) {
		return (ObjectServerSocketMock) _serverSocketByPort.get(new Integer(serverPort));
	}

}
