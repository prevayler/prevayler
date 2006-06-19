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

public class OldNetworkImpl implements OldNetwork {

    public ObjectSocket openSocket(String serverIpAddress, int serverPort) throws IOException {
        return new ObjectSocketImpl(serverIpAddress, serverPort);
    }

    public ObjectServerSocket openObjectServerSocket(int port) throws IOException {
        return new ObjectServerSocketImpl(port);
    }
}
