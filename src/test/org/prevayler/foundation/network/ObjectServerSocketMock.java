//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Alexandre Nodari.

package org.prevayler.foundation.network;

import java.io.IOException;

import org.prevayler.foundation.Cool;


public class ObjectServerSocketMock implements ObjectServerSocket {

	private boolean _isWaiting;
	private ObjectSocket _counterpart;

	public synchronized ObjectSocket accept() throws IOException {
		if (_isWaiting) throw new IOException("Port already in use.");
	
		_isWaiting = true;
		Cool.wait(this);
		_isWaiting = false;
		
		ObjectSocket result = _counterpart;
		_counterpart = null;
		return result;
	}

	synchronized ObjectSocket openClientSocket() throws IOException {
		if (!_isWaiting) throw new IOException("No thread is accepting connections on this port.");
		ObjectSocketMock result = new ObjectSocketMock();
		_counterpart = result.counterpart();
		notify();
		return result;
	}
}
