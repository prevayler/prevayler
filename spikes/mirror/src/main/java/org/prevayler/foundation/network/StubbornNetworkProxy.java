/*
 * StubbornNetworkProxy.java
 *
 *
 */
package org.prevayler.foundation.network;

import java.io.IOException;


/**
 * Stubborn Network Proxies are bound to each Service instance in 
 * a Prevayler providing Server services for Replication.
 * 
 * This allows the binding to be dynamically connected to a Client
 * replicator, thereby facilitating reconnection after failure.
 * 
 * A failed receive causes the proxy to capture the thread until
 * the replication client (network) reconnects.
 * 
 * The Proxy never invokes "close" on the service layer.
 *
 */
public class StubbornNetworkProxy implements ObjectReceiver {
    private ObjectReceiver _network;
    private ObjectReceiver _client;
    private StubbornNetworkClientConnector _inboundChannel;
    
    public StubbornNetworkProxy () {
    }
    
    protected synchronized void connect(ObjectReceiver network,
                                        StubbornNetworkClientConnector inboundChannel ) {
        _network = network;
        _inboundChannel = inboundChannel;
        openClientChannel();
    }
    
    protected synchronized void disconnect () {
        _inboundChannel = null;
        _network = null;
    }
    
    protected void setClient(ObjectReceiver client) {
        _client = client;
    }
    
    public ObjectReceiver getClient() {
        return _client;
    }

    public void receive(Object object) {
        if (isOpen()) {
            attemptToForward(object);
        } else {
            waitTillOpen();
            attemptToForward(object);
        }
    }
    
    private synchronized boolean forwarded (Object object) {
        try {
            if (_network == null) {
                return false;
            }
            _network.receive(object);
            return true;
        } catch (IOException handle) {
            return false;
        }
    }
    
    private void attemptToForward(Object object) {
        while (!forwarded(object)) {
            closeClientChannel();
            waitTillOpen();
        }
    }
    
    private synchronized void waitTillOpen() {
        try {
            wait();
        } catch (InterruptedException unexpected) {
            //TODO: Action TBD
        }
    }
    
    
    private synchronized boolean isOpen() {
        return (_network != null);
    }
    
    public void close() throws IOException {
        closeClientChannel();
        _client = null;
    }
    
    private synchronized void openClientChannel() {
        notify();
    }

    private void closeClientChannel() {
        try {
            if (_inboundChannel != null) _inboundChannel.disconnect();
            if (_network != null) _network.close();
        } catch (IOException ignorable) {
        } finally {
            _network = null;
            _inboundChannel = null;
        }
    }
    
}
