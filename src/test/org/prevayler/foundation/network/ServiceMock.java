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

    private Map<Integer, ObjectReceiver> clients = new HashMap<Integer, ObjectReceiver>();

    private Map<Integer, ObjectReceiver> serverMock = new HashMap<Integer, ObjectReceiver>();

    private int index = 1;

    public synchronized ObjectReceiver serverFor(ObjectReceiver client) {
        ObjectReceiver mock = new ObjectReceiverMock();
        serverMock.put(index, mock);
        clients.put(index, client);
        index++;
        return mock;
    }

    public void close(int service) throws IOException {
        clients.remove(service).close();
    }

    public ObjectReceiver getServerNetwork(int key) {
        ObjectReceiver receiver = clients.get(key);
        while (receiver == null) {
            Cool.sleep(1);
            receiver = clients.get(key);
        }
        return receiver;
    }

    public ObjectReceiverMock getServerMock(int key) {
        return (ObjectReceiverMock) serverMock.get(key);
    }

    public void reset() {
        index = 1;
    }

}
