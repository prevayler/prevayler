/*
 * StubbornServerReceiverImpl.java
 *
 * Copyright (c) 2004 MoneySwitch Ltd.
 * Level 5, 55 Lavender St, Milsons Point 2061.
 * All rights reserved.
 *
 */
package org.prevayler.foundation.network;

import java.io.IOException;

import org.prevayler.foundation.Cool;


public class StubbornServerReceiverImpl implements ObjectReceiver {

    private ObjectReceiver _provider; /* the network layer */
    private ObjectReceiver _client;   /* the replicator    */
    private Service _clientService;
    private StubbornServiceImpl _stubbornService;
    private boolean _isOpen;
    private Object _unsentMessage;
    /** 
     * Provider has called in with a connection, need to get it going.
     * First, I wait for their session id. If it matches an existing 
     * session, I hook them back together, if it is not found (ugh) or
     * if it is zero (new session no history), I send them a new id.
     */

    public StubbornServerReceiverImpl(ObjectReceiver provider, Service service, StubbornServiceImpl stubbornService) {
        _provider = provider;
        _clientService = service;
        _stubbornService = stubbornService;
        _isOpen = false;
    }

    public void receive(Object object) throws IOException {
        if (object instanceof IOException) {
            closeSession();
            return;
        }
        if (_isOpen) {
            _client.receive(object);
            return;
        } 
        int sessionId = ((Integer)object).intValue();
        if (sessionId == 0) {
            establishNewSession();
        } else {
            reestablishSession(sessionId);
        }
    }

    private void establishNewSession() {
        _client = _clientService.serverFor(new ClientProxy(this));
        int sessionId = _stubbornService.add(_client);
        send(sessionId);
    }
    
    private void reestablishSession (int sessionId) {
        _client = _stubbornService.find(sessionId);
        if (_client == null) {
            establishNewSession();
        } else {
            send(sessionId);
            if (!_isOpen) {
                return;
            }
            if (_unsentMessage != null) {
                Object unsent = _unsentMessage;
                _unsentMessage = null;
                send(unsent);
            }
        }
    }
    
    private void closeSession () {
        try {
            _provider.close();
        } catch (IOException ignore) {
        } finally {
            _isOpen = false;
        }
    }
    
    private void send(int sessionId) {
        try {
            _provider.receive(new Integer(sessionId));
            open();
        } catch (IOException unex) {
            closeSession();
        }
    }
    
    private void send (Object object) {
        try {
            _provider.receive(object);
        } catch (IOException unex) {
            _unsentMessage = object;
            closeSession();
        }
    }
    
    public void clientRequestsReceive(Object object) {
        waitTillOpen();
        send(object);
    }
    
    private synchronized void waitTillOpen() {
        if (!_isOpen) {
            Cool.wait(this);
        }
    }
    
    private synchronized void open() {
        _isOpen = true;
        notify();
    }

    public void clientRequestsClose() {
        System.out.println("Client (Server Replicator) Requested Close");
        
    }
    /* (non-Javadoc)
     * @see org.prevayler.foundation.network.ObjectReceiver#close()
     */
    public void close() throws IOException {
        System.out.println("Network (server) Requested Close");
    }
    class ClientProxy implements ObjectReceiver {

        private StubbornServerReceiverImpl _controller;

        public ClientProxy (StubbornServerReceiverImpl controller) {
            _controller = controller;
        }
        public void receive(Object object) throws IOException {
            _controller.clientRequestsReceive(object);
        }
        public void close() throws IOException {
            _controller.clientRequestsClose();
        }
        
    }

}
