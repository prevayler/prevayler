//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Alexandre Nodari.

package org.prevayler.foundation.network;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.prevayler.foundation.Cool;


public class ObjectSocketMock implements ObjectSocket {

	private ObjectSocketMock _counterpart;
	private List _receivedObjects = new LinkedList();
	private Permit _permit;

	ObjectSocketMock(Permit permit) {
		initialize(permit, new ObjectSocketMock(this, permit));
	}

	private ObjectSocketMock(ObjectSocketMock counterpart, Permit permit) {
		initialize(permit, counterpart);
	}

	private void initialize(Permit permit, ObjectSocketMock counterpart) {
		_permit = permit;
		_permit.addObjectToNotify(this);
		_counterpart = counterpart;
	}

	public void writeObject(Object object) throws IOException {
		_permit.check();
		_counterpart.receive(object);
	}

	private synchronized void receive(Object object) {
		_receivedObjects.add(object);
		notify();
	}

	public synchronized Object readObject() throws IOException, ClassNotFoundException {
		_permit.check();
		if (_receivedObjects.isEmpty()) Cool.wait(this);
		_permit.check();
		return _receivedObjects.remove(0);
	}

	public void close() throws IOException {
		//TODO Implement this close.
	}

	public ObjectSocket counterpart() {
		return _counterpart;
	}
	
}
