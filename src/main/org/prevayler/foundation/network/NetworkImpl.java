//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful,
//     but WITHOUT ANY WARRANTY; without even the implied warranty of 
//     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//     See the license distributed along with this file for more details.
//Contributions: anon.

package org.prevayler.foundation.network;

import java.io.IOException;

/**
 * Provides a Basic Network Service, no recovery of failure, and no
 * reconnect support.
 */

public class NetworkImpl extends BaseNetworkImpl {

    public ObjectReceiver newReceiver (String ipAddress, int port,
            ObjectReceiver client) throws IOException {
        return new NetworkClientObjectReceiverImpl(ipAddress, port, client);
    }
    
    
    public ObjectReceiver newReceiver(Service service, ObjectSocket socket) throws IOException {
        return new NetworkClientObjectReceiverImpl(socket,service);
    }

}
