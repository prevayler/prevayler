package org.prevayler.foundation.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.prevayler.foundation.Cool;


/**
 * Useful class comments should go here
 *
 * $Revision: 1.1 $
 * $Date: 2005/02/16 04:28:34 $
 * $Author: peter_mxgroup $
 */
public class StubbornServiceImpl extends Thread 
                                 implements Service {

    private Network _provider;
    private Service _service;
    private int _port;
    private boolean _wantedOpen;
    private int _masterSessionId = 100;
    private HashMap _sessions = new HashMap();

    
    public ObjectReceiver serverFor(ObjectReceiver client) {
        return new StubbornServerReceiverImpl(client, _service, this);
    }
    
    public synchronized ObjectReceiver find (int sessionId) {
        return (ObjectReceiver) _sessions.get(new Integer(sessionId));
    }
    
    public synchronized int add (ObjectReceiver receiver) {
        int key = _masterSessionId++;
        _sessions.put(new Integer(key), receiver);
        return key;
    }

    public StubbornServiceImpl(Network provider, Service service, int port) {
        this._provider = provider;
        this._service = service;
        this._port = port;
        _wantedOpen = true;
        setName("Prevayler Stubborn Service");
        setDaemon(true);
        start();
        
    }
    
    public void run() {
        while (_wantedOpen) {
            try {
                _provider.startService(this, _port);
                sleepTillShutdown();
            } catch (IOException iox) {
                Cool.sleep(1000);   /* try again every second */
            }
        }
    }
  
    private synchronized void sleepTillShutdown() {
        Cool.wait(this);
        stopProviderService();
        closeSessions();
        _sessions.clear();
    }

    private void stopProviderService() {
        try {
            _provider.stopService(_port);
        } catch (IOException unExpected) {
            // not much I can do just swallow
        }
    }

    public synchronized void shutdown() {
        _wantedOpen = false;
        this.notify();
    }

    /**
     * 
     */
    private void closeSessions() {

        Collection sessions = _sessions.values();
        Iterator iter = sessions.iterator();
        while (iter.hasNext()) {
            ObjectReceiver localClient = (ObjectReceiver) iter.next();
            closeClient(localClient);
        }
    }

    private void closeClient(ObjectReceiver localClient) {
        try {
            localClient.close();
        } catch (IOException cantHappen) {
            // igonre the client
        }
    }
}
