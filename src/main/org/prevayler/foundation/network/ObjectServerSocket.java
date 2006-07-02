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
import java.net.ServerSocket;

public class ObjectServerSocket {

    private final ServerSocket _serverSocket;

    public ObjectServerSocket(int port) throws IOException {
        _serverSocket = new ServerSocket(port);
    }

    public ObjectSocket accept() throws IOException {
        return new ObjectSocket(_serverSocket.accept());
    }

    public void close() throws IOException {
        _serverSocket.close();
    }

}
