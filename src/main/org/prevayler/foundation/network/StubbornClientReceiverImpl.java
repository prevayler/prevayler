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
    private Sleep shutdownMonitor = new Sleep();

    public StubbornClientReceiverImpl(Network network,
                                      String ipAddress,
                                      int port,
                                      ObjectReceiver client) {
        this._ipAddress = ipAddress;
        this._port = port;
        this._client = client;
        this._network = network;
        this._networkProxy = new NetworkProxy(this);
        this.setName("Prevayler Stubborn Client " + port);
        setDaemon(true);
        this._wantedOpen = true;
        this._sessionState = CLOSED;
        this._sessionKey = 0;
        start();
        
    }
    /** 
     * Wait for the connection to occur before proceeding
     */
    public void networkRequestToReceive(Object object) throws IOException {
        if (sessionIsConnected()) {
            this._client.receive(object);
        } else {        // must be the session id token
            receiveSessionKey(object);
            wakeUpSleepingClient();
        }
    }

    private void receiveSessionKey (Object sessionKey) {
        this._sessionKey = ((Integer)sessionKey).intValue();
    }
    
    private synchronized void wakeUpSleepingClient() {
        this._sessionState = CONNECTED;
        notify();
    }
    
   
    private void closeForReconnect() {
        try {
            this._provider.close();
        } catch (IOException unExpected) {
            // Do Nothing
        } finally {
            this._sessionState = CLOSED;
        }
    }
    
    
    public void networkRequestsClose() {
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

    /**
     * @param object
     */
    private void forwardToProvider(Object object) {
        try {
            this._provider.receive(object);
        } catch (IOException unExpected) {
            closeForReconnect();
        }
    }
    
    public void close() throws IOException {
        this._wantedOpen = false;
        shutdown();
    }

    private void shutdown() {
        if (sessionIsOpen()) {
            this.shutdownMonitor.wakeUp();   
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
                this._provider = _network.findServer(_ipAddress,_port,_networkProxy);
                sendSessionKey();
                sleepMyself();
            } catch (IOException iox) {
                Cool.sleep(1000);   /* try again every second */
            }
    }
    
    private void sendSessionKey() {
        this._sessionState = OPEN;
        forwardToProvider(new Integer(_sessionKey));
    }
    
    private void sleepMyself() {
        this.shutdownMonitor.goToSleep();
    }
    class NetworkProxy implements ObjectReceiver {

        private StubbornClientReceiverImpl _controller;

        public NetworkProxy (StubbornClientReceiverImpl controller) {
            this._controller = controller;
        }
        public void receive(Object object) throws IOException {
            this._controller.networkRequestToReceive(object);
        }
        public void close() throws IOException {
            this._controller.networkRequestsClose();
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
