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
 * an embedded ObjectReceiver to the Provider (NetworkProxy).
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
    private ObjectReceiver _provider;
    private Network _network;
    private String _ipAddress;
    private int _port;
    private volatile boolean _wantedOpen;
    private NetworkProxy _networkProxy;
    private int _sessionKey;
    private int _sessionState;
    private Sleep _shutdownMonitor = new Sleep();

    public StubbornClientReceiverImpl(Network network,
                                      String ipAddress,
                                      int port,
                                      ObjectReceiver client) {
        _ipAddress = ipAddress;
        _port = port;
        _client = client;
        _network = network;
        _networkProxy = new NetworkProxy(this);
        _wantedOpen = true;
        _sessionState = CLOSED;
        _sessionKey = 0;
        super.setName("Prevayler Stubborn Client " + port);
        super.setDaemon(true);
        super.start();
        
    }
    /** 
     * Wait for the connection to occur before proceeding
     */
    protected void networkRequestToReceive(Object object) throws IOException {
        if (object instanceof IOException) {
            closeForReconnect();
            _shutdownMonitor.wakeUp();
            return;
        } 
        if (sessionIsConnected()) {
            _client.receive(object);
        } else {        // must be the session id token
            receiveSessionKey(object);
            wakeUpSleepingClient();
        }
    }

    private void receiveSessionKey (Object sessionKey) {
        _sessionKey = ((Integer)sessionKey).intValue();
    }
    
    
    private synchronized void wakeUpSleepingClient() {
        _sessionState = CONNECTED;
        notify();
    }
    
   
    private void closeForReconnect() {
        try {
            _provider.close();
        } catch (IOException ignore) {
            // Do Nothing
        } finally {
            _sessionState = CLOSED;
        }
    }
    
    
    protected void networkRequestsClose() {
        System.out.println("Network (client) requests close");
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
            _provider.receive(object);
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
            _shutdownMonitor.wakeUp();   
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
                _provider = _network.findServer(_ipAddress,_port,_networkProxy);
                sendSessionKey();
                sleepMyself();
            } catch (IOException iox) {
                closeForReconnect();
                Cool.sleep(1000);   /* try again every second */
            }
    }
    
    private void sendSessionKey() throws IOException {
        _provider.receive(new Integer(_sessionKey));
        _sessionState = OPEN;
    }
    
    private void sleepMyself() {
        _shutdownMonitor.goToSleep();
    }
    class NetworkProxy implements ObjectReceiver {

        private StubbornClientReceiverImpl _controller;

        public NetworkProxy (StubbornClientReceiverImpl controller) {
            _controller = controller;
        }
        public void receive(Object object) throws IOException {
            _controller.networkRequestToReceive(object);
        }
        public void close() throws IOException {
            _controller.networkRequestsClose();
        }
        
    }

    class Sleep {
        public synchronized void goToSleep() {
            Cool.wait(this);
        }
        
        public synchronized void wakeUp() {
            notify();
        }
    }
}
