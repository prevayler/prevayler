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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServiceMock implements Service {

    private Map clients = new HashMap(); // the network receiver

    private Map serverMock = new HashMap(); // the testcode receiver

    private int index = 1;

    public synchronized ObjectReceiver serverFor(ObjectReceiver client) {
        Integer key = new Integer(index);
        ObjectReceiver mock = new ObjectReceiverMock();
        serverMock.put(key, mock);
        clients.put(key, client);
        index++;
        return mock;
    }

    public void close(int service) throws IOException {
        ((ObjectReceiver) clients.remove(new Integer(service))).close();
    }

    public ObjectReceiver getServerNetwork(int key) {
        Integer theKey = new Integer(key);
        Object receiver = clients.get(theKey);
        while (receiver == null) {
            Cool.sleep(1);
            receiver = clients.get(theKey);
        }
        return (ObjectReceiver) receiver;
    }

    public ObjectReceiverMock getServerMock(int key) {
        return (ObjectReceiverMock) serverMock.get(new Integer(key));
    }

    public void reset() {
        index = 1;
    }
}
