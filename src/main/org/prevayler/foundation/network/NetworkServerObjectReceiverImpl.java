//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the license distributed along with this file for more details.
//Contributions: anon.
package org.prevayler.foundation.network;

import java.io.IOException;
import java.net.SocketException;

/**
 * Provides a server connection service.
 * 
 * Uses a thread to wait for connections. It then creates a new instance of
 * a Receiver. 
 */


public class NetworkServerObjectReceiverImpl extends Thread implements NetworkServerObjectReceiver  {


    private Service _service;
    private ObjectServerSocket _provider;
    private boolean _wantedOpen;
    private NetworkReceiverFactory _factory;

    public NetworkServerObjectReceiverImpl (NetworkReceiverFactory factory, Service service, int port) throws IOException{
        this(factory, service, new ObjectServerSocketImpl(port));
    }
    
    protected NetworkServerObjectReceiverImpl(NetworkReceiverFactory factory, Service service, ObjectServerSocket server) {
        _factory = factory;
        _service = service;
        _provider = server;
        _wantedOpen = true;
        setName("Prevayler Network Server Receiver");
        setDaemon(true);
        start();
    }
    
    public void run () {
        while (_wantedOpen) {
            try {
                _factory.newReceiver(_service, _provider.accept());
            } catch (SocketException sox) {
                _wantedOpen = false;
                // socket closed so exit
            } catch (IOException iox) {
                // ignore and continue to connect
            }
        }
    }
    
    public void shutdown() {
        try {
            _wantedOpen = false;
            this._provider.close();
        } catch (IOException ex) {
            // can't do much, so ignore
        }
    }
    
}
