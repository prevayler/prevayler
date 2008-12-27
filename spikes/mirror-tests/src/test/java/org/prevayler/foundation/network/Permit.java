//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Alexandre Nodari.

package org.prevayler.foundation.network;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class Permit {
	
	boolean _isValid = true;
	private final Set _objectsToNotify = new HashSet();
	
	public void check () throws IOException {
		if (! _isValid)
			throw new IOException("Network crash simulated.");
	}
	
	public void expire() {
		_isValid = false;
		Iterator it = _objectsToNotify.iterator();
		while (it.hasNext()) {
			Object toNotify = it.next();
			synchronized (toNotify) {
				toNotify.notify();
			}
		}
	}

	public void addObjectToNotify(Object object) {
		_objectsToNotify.add(object);
	}
}
