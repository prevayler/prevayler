//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful,
//     but WITHOUT ANY WARRANTY; without even the implied warranty of 
//     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//     See the license distributed along with this file for more details.
//Contributions: anon.

package org.prevayler.foundation.network;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Provides a Basic Network Service, no recovery of failure, and no
 * reconnect support.
 */

public class NetworkImpl implements Network {
    private final Map _providerByPort = new Hashtable();

    public void startService(Service service, int port) throws IOException {
        checkNotInUse(port);

        NetworkServerObjectReceiverImpl provider = new NetworkServerObjectReceiverImpl(service, port);
        _providerByPort.put(new Integer(port), provider);
    }

    public ObjectReceiver findServer(String ipAddress, int port,
            ObjectReceiver client) throws IOException {
        return new NetworkClientObjectReceiverImpl(ipAddress, port, client);
    }
    
    public void stopService (int port) throws IOException {
        NetworkServerObjectReceiverImpl provider = removeServer(port); 
        if (provider == null) {
            throw new IOException("illegal port number used");
        }
        provider.shutdown();
    }
    private void checkNotInUse(int serverPort) throws IOException  {
        Object old = _providerByPort.get(new Integer(serverPort));
        if (old != null) throw new IOException("Port already in use.");
    }
    private NetworkServerObjectReceiverImpl removeServer(int serverPort) {
        return (NetworkServerObjectReceiverImpl) _providerByPort.remove(new Integer(serverPort));
    }

}
