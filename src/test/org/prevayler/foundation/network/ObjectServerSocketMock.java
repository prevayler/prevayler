//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Alexandre Nodari.

package org.prevayler.foundation.network;

import java.io.IOException;

import org.prevayler.foundation.Cool;


public class ObjectServerSocketMock implements ObjectServerSocket {

	private ObjectSocket _clientSide;
	private final Permit _permit;


	public ObjectServerSocketMock(Permit permit) {
		_permit = permit;
		_permit.addObjectToNotify(this);
	}

	public synchronized ObjectSocket accept() throws IOException {
		_permit.check();
		
		if (_clientSide != null) throw new IOException("Port already in use.");
		ObjectSocketMock result = new ObjectSocketMock(_permit);
		_clientSide = result.counterpart();
		
		notifyAll(); //Notifies all client threads.
		Cool.wait(this);

		_permit.check();
		return result;
	}

	synchronized ObjectSocket openClientSocket() throws IOException {
		_permit.check();
		while (_clientSide == null) Cool.wait(this);
		_permit.check();

        ObjectSocket result = _clientSide;
        _clientSide = null;
        notifyAll(); //Notifies the server thread (necessary) and eventual client threads (harmless).
        return result;
	}

	public void close() throws IOException {
		// TODO Auto-generated method stub
	}

}
