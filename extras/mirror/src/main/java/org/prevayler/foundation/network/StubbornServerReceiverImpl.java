/*
 * StubbornServerReceiverImpl.java
 *
 *
 */
package org.prevayler.foundation.network;

import java.io.IOException;

/**
 * Inbound channel for a Server's network.
 * 
 * Completes SessionId exchange in order to reconnect to the correct
 * session. Informs the outbound channel of network reconnection.
 * 
 * Forwards inbound data direct to the client. If an error occurs, closes
 * the session and notifies the outbound channel (StubbornNetworkProxy). 
 * 
 *  
 */

public class StubbornServerReceiverImpl implements ObjectReceiver, StubbornNetworkClientConnector {

    private ObjectReceiver _provider; /* the network layer */
    private ObjectReceiver _client;   /* the replicator    */
    private StubbornNetworkProxy _proxy;  /* the proxy for the client */
    private Service _clientService;
    private SessionsManager _sessionsManager;
    private boolean _isOpen;
    private NetworkSessionId _sessionId;

    /** 
     * Provider has called in with a connection, need to get it going.
     * First, I wait for their session id. If it matches an existing 
     * session, I hook them back together, if it is not found (ugh) or
     * if it is zero (new session no history), I send them a new id.
     */

    public StubbornServerReceiverImpl(ObjectReceiver provider, Service service, SessionsManager stubbornService) {
        _provider = provider;
        _clientService = service;
        _sessionsManager = stubbornService;
        _isOpen = false;
    }
    

    public StubbornServerReceiverImpl(Service service, ObjectSocket socket, SessionsManager sessionsManager) {
        _clientService = service;
        _sessionsManager = sessionsManager;
        _provider = new NetworkClientObjectReceiverImpl(socket, this);
        _isOpen = false;
    }


    public void receive(Object object) throws IOException {
        if (_isOpen) {
            _client.receive(object);
        } else {
            handleSession(object);
        }
    }

    public synchronized void close() throws IOException {
        _isOpen = false;
        if (_proxy != null) _proxy.disconnect();
    }

    
    private void handleSession (Object object) {
        if (!(object instanceof NetworkSessionId)) {
            closeSession();
        }
        StubbornNetworkProxy proxy = reestablishSession((NetworkSessionId)object);
        _isOpen = true;
        send(_sessionId);
        if (!_isOpen) {
            return;
        }
        _proxy = proxy;
        _proxy.connect(_provider, this);
    }

    private StubbornNetworkProxy establishNewSession() {
        StubbornNetworkProxy proxy = new StubbornNetworkProxy();
        _client = _clientService.serverFor(proxy);
        proxy.setClient(_client);
        _sessionId = _sessionsManager.add(proxy);
        return proxy;
    }
    
    private StubbornNetworkProxy reestablishSession (NetworkSessionId sessionId) {
        StubbornNetworkProxy proxy = _sessionsManager.find(sessionId);
        if (proxy == null) {
            proxy = establishNewSession();
        } else {
            _sessionId = sessionId;
        }
        return proxy;
    }
    
    private void closeSession () {
        try {
            _provider.close();
        } catch (IOException ignore) {
        } finally {
            _isOpen = false;
        }
    }
    
    private void send (Object object) {
        try {
            _provider.receive(object);
        } catch (IOException unex) {
            closeSession();
        }
    }


    public void disconnect() {
        _sessionsManager.remove(_sessionId);
        _sessionId = null;
        _proxy = null;
        _isOpen = false;
    }
    
}
