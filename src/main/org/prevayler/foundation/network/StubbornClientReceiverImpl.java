/*
 * StubbornClientReceiverImpl.java
 *
 *
 */
package org.prevayler.foundation.network;

import java.io.IOException;

import org.prevayler.foundation.Cool;


/**
 * The StubbronClientReceiver is an ObjectReceiver to the Client and has
 * a connection to the Socket (Provider).
 * 
 * It uses its being in the middle of the exchange to implement a recovery
 * mechanism on session failure.
 * 
 * At startup the Replication Client, sends its current session identifier
 * to the Replication Server, which if it matches a prior session, it 
 * reconnects the session. It sends a new session identifier. 
 * 
 * If the client sends a zero session identifier it assumes it is a new 
 * connection and replies with a new session identifier to use in subsequent 
 * connections.
 */
public class StubbornClientReceiverImpl extends Thread implements ObjectReceiver {

    private static int CLOSED = 0;     /* the connection is closed */
    private static int OPEN   = 1;     /* open but hasn't exchanged token */
    private static int CONNECTED = 2;  /* have exchanged token */
    private ObjectReceiver _client;
    private ObjectSocket   _socketProvider;
    private StubbornNetwork _stubbornNetwork;
    private String _ipAddress;
    private int _port;
    private volatile boolean _wantedOpen;
    private NetworkSessionId _sessionKey;
    private int _sessionState;

    public StubbornClientReceiverImpl(StubbornNetwork stubbornNetwork,
                                      String ipAddress,
                                      int port,
                                      ObjectReceiver client) {
        _stubbornNetwork = stubbornNetwork;
        _ipAddress = ipAddress;
        _port = port;
        _client = client;
        _wantedOpen = true;
        _sessionState = CLOSED;
        _sessionKey = new NetworkSessionId(0,0);
        
        super.setName("Prevayler Stubborn Client " + port);
        super.setDaemon(true);
        super.start();
        
    }
    
    public StubbornClientReceiverImpl(ObjectSocket socket, Service service ) {
        
    }
    /** 
     * Wait for the connection to occur before proceeding
     */
    protected void networkRequestToReceive(Object object) throws IOException {
        if (sessionIsConnected()) {
            _client.receive(object);
        } else {        // must be the session id token
            receiveSessionKey(object);
            wakeUpSleepingClient();
        }
    }

    private void receiveSessionKey (Object sessionKey) {
        _sessionKey = (NetworkSessionId)sessionKey;
    }
    
    
    private synchronized void wakeUpSleepingClient() {
        _sessionState = CONNECTED;
        notify();
    }
    
   
    private void closeForReconnect() {
        try {
            _socketProvider.close();
        } catch (IOException ignore) {
            // Do Nothing
        } finally {
            _sessionState = CLOSED;
        }
    }

    public void receive(Object object) throws IOException {
        waitIfNotOpen();
        forwardToProvider(object);
    }

    private synchronized void waitIfNotOpen() {
        try {
            if (!sessionIsConnected()) {
                wait();
            }
        } catch (InterruptedException unEx) {
            //Uh-oh
        }
    }

    private void forwardToProvider(Object object) {
        try {
            _socketProvider.writeObject(object);
        } catch (IOException unExpected) {
            shutdown();
            waitToResend(object);
        }
    }
    
    private synchronized void waitToResend(Object object) {
        try {
            wait();
            forwardToProvider(object);
        } catch (InterruptedException uhOh) {
            //TODO: ????
        }
    }
    public void close() throws IOException {
        _wantedOpen = false;
        shutdown();
    }

    private void shutdown() {
        if (sessionIsOpen()) {
            closeForReconnect();
        }
    }
    
    private boolean sessionIsOpen() {
        return (_sessionState > CLOSED);
    }
    
    private boolean sessionIsConnected() {
        return (_sessionState == CONNECTED);
    }
    public void run () {
        while (_wantedOpen)
            try {
                _socketProvider = _stubbornNetwork.newInstance(_ipAddress, _port);
                sendSessionKey();
                startReading();
            } catch (IOException iox) {
                closeForReconnect();
                Cool.sleep(1000);   /* try again every second */
            }
    }
    
    private void sendSessionKey() throws IOException {
        _socketProvider.writeObject(_sessionKey);
        _sessionState = OPEN;
    }

    private void startReading() throws IOException{
        while (_wantedOpen) {
            try {
                Object o = _socketProvider.readObject();
                networkRequestToReceive(o);
            } catch (ClassNotFoundException returnIt) {
                _socketProvider.writeObject(returnIt);
            }
        }
    }
}
