//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful,
//   but WITHOUT ANY WARRANTY; without even the implied warranty of 
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//   See the license distributed along with this file for more details.
//Contributions: anon.
package org.prevayler.foundation.network;

import java.io.IOException;


public class NetworkClientObjectReceiverImpl implements ObjectReceiver {

    /** our connection to the network */
    private ObjectSocket _provider;
    /** the upper layer who we give what we receive to */
    private ObjectReceiver _client;

    private volatile boolean _closing;
    
    public NetworkClientObjectReceiverImpl (String ipAddress, int port,
            ObjectReceiver client) throws IOException {
        this(new ObjectSocketImpl(ipAddress, port), client);
    }
    public NetworkClientObjectReceiverImpl(ObjectSocket objectSocket,
                                           Service service) throws IOException {
        _provider = objectSocket;
        _client = service.serverFor(this);
        startReading();
    }
    
    public NetworkClientObjectReceiverImpl (ObjectSocket objectSocket,
                                            ObjectReceiver client) {
        _provider = objectSocket;
        _client = client;
        startReading();
        
    }
    
    /**
     * Create a reader thread
     *
     */
    private void startReading()  {
        Thread reader = new Thread() {
            public void run() {
                while (!_closing) receiveFromNetwork();
            }
        };
        reader.setName("Prevayler Network Client Receiver");
        reader.setDaemon(true);
        reader.start();
    }
    
    /**
     * When something is read give it to our client, also give it the exception
     */
    private void receiveFromNetwork() {
        try {
            Object object = _provider.readObject();
            passToClient(object);
        } catch (Exception ex) {
            closeDown();
            if (_closing) {
                return;
            }
            passToClient(ex);
        } 
    }
    
    private void closeDown() {
        try {
            close();
        } catch (IOException ignorable) {}
        try {
            _client.close();
        } catch (IOException neverThrown) {}
    }

    private void passToClient(Object object) {
        try {
            _client.receive(object);
        } catch (IOException unExpected) {
            unExpected.printStackTrace();
        }
    }
    /**
     * Receive an object from prevayler client to be sent to the network
     */
    public void receive(Object object) throws IOException {
        _provider.writeObject(object);
    }

    /* (non-Javadoc)
     * @see org.prevayler.foundation.network.ObjectReceiver#close()
     */
    public void close() throws IOException {
        _closing = true;
        _provider.close();
    }
}
