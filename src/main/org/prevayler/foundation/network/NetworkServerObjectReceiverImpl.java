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


public class NetworkServerObjectReceiverImpl extends Thread  {


    private Service _service;
    private ObjectServerSocket _provider;

    public NetworkServerObjectReceiverImpl (Service service, int port) throws IOException{
        this(service, new ObjectServerSocketImpl(port));
    }
    
    public NetworkServerObjectReceiverImpl(Service service, ObjectServerSocket server) {
        this._service = service;
        this._provider = server;
        setName("Prevayler Network Server Receiver");
        setDaemon(true);
        start();
    }
    
    public void run () {
        try {
            while (true) new ServerConnection(_service, _provider.accept());
        } catch (SocketException sox) {
            // socket closed so exit

        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }
    
    public void shutdown() {
        try {
            this._provider.close();
        } catch (IOException ex) {
            // can't do much, so ignore
        }
    }
    
    class ServerConnection  {

        private ObjectReceiver _client;
        
        public ServerConnection (Service service,
                                 ObjectSocket socket) throws IOException {
            
            this._client = new NetworkClientObjectReceiverImpl(socket,service);
            
            
        }
    }

}
