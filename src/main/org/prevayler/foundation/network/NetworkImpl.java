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

import java.io.IOException;

/**
 * Provides a Basic Network Service, no recovery of failure, and no reconnect
 * support.
 */

public class NetworkImpl extends BaseNetworkImpl {

    @Override public ObjectReceiver newReceiver(String ipAddress, int port, ObjectReceiver client) throws IOException {
        return new NetworkClientObjectReceiverImpl(ipAddress, port, client);
    }

    @Override public ObjectReceiver newReceiver(Service service, ObjectSocket socket) {
        return new NetworkClientObjectReceiverImpl(socket, service);
    }

}
