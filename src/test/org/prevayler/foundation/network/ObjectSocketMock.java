// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.foundation.network;

import org.prevayler.foundation.Cool;
import org.prevayler.foundation.DeepCopier;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
        _counterpart.receive(DeepCopier.deepCopy(object));
    }

    private synchronized void receive(Object object) {
        _receivedObjects.add(object);
        notify();
    }

    public synchronized Object readObject() throws IOException {
        _permit.check();
        if (_receivedObjects.isEmpty())
            Cool.wait(this);
        _permit.check();
        return _receivedObjects.remove(0);
    }

    public void close() {
        // TODO Implement this close.
    }

    public ObjectSocket counterpart() {
        return _counterpart;
    }

}
