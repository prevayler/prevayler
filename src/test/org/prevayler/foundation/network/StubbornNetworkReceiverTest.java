/*
 * StubbornNetworkReceiverTest.java
 *
 * Copyright (c) 2005 MoneySwitch Ltd.
 * Level 5, 55 Lavender St, Milsons Point 2061.
 * All rights reserved.
 *
 */
package org.prevayler.foundation.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;


/**
 * Useful class comments should go here
 *
 * $Revision: 1.1 $
 * $Date: 2005/02/25 04:29:29 $
 * $Author: peter_mxgroup $
 */
public class StubbornNetworkReceiverTest extends TestCase {

    private ObjectReceiverMock provider;
    private ObjectReceiverMock client;
    private SimpleNetworkMock network;
    private StubbornClientReceiverImpl scri;

    public void setUp () throws Exception {
        provider = new ObjectReceiverMock();
        client   = new ObjectReceiverMock();
        network      = new SimpleNetworkMock();
        network.setProvider(provider);
        scri = new StubbornClientReceiverImpl(network, "localhost", 9876, client);
        Thread.yield();
    }
    
    private void endConnection() throws Exception {
        scri.close();
        Thread.yield();
        assertTrue(provider.isClosed());
        
    }
    
    private void checkForSessionId(int id) {
        Object received = provider.selfCheck();
        if (received instanceof Integer) {
            assertEquals(new Integer(id),received);
        } else {
            fail ("Did not receive the Session Id");
        }
    }

    private void startSession (int sessionId) {
        network.releaseServer();
        Thread.yield();
        checkForSessionId(sessionId);
    }
    
    private void completeSession (int sessionId) throws Exception {
        scri.networkRequestToReceive(new Integer(sessionId));
        Thread.yield();
    }
    
    private void establishSession(int initialSessionId) throws Exception{
        startSession(initialSessionId);
        completeSession(100);
    }

    public void testSessionStart() throws Exception {
        startSession(0);
        endConnection();
    }
    
    public void testSessionAbort() throws Exception {
        provider.receiveCrash();
        network.releaseServer();
        Thread.yield();
        assertTrue(provider.isClosed());
        Thread.sleep(1100); /* the client waits a second to retry */
        startSession(0);
        endConnection();
    }
    
    public void testSessionFailResponse() throws Exception {
        startSession(0);
        scri.networkRequestToReceive(new IOException("Network Lost"));
        Thread.yield();
        assertTrue(provider.isClosed());
        Thread.sleep(1100);
        startSession(0);
        endConnection();
    }
    
    public void testFailAfterSessionEstablished() throws Exception {
        establishSession(0);
        scri.networkRequestToReceive(new IOException("Network Lost"));
        Thread.yield();
        assertTrue(provider.isClosed());
        startSession(100);
        endConnection();
    }
    public void testGoodReceiving() throws Exception {
        establishSession(0);
        String object1 = "A";
        scri.networkRequestToReceive(object1);
        client.check(object1);
        endConnection();
    }
    
    

    public void testSending() throws Exception {
        establishSession(0);
        String object1 = "A";
        scri.receive(object1);
        provider.check(object1);
        endConnection();
    }
    
    
    public void testSendIOException() throws Exception {
        MockClientSender sender = new MockClientSender(scri);
        Thread.yield();
        establishSession(0);
        String object1 = "A";
        provider.receiveCrash();
        sender.send(object1);
        Thread.yield();
        assertTrue(provider.isClosed());
        Thread.yield(); /* the client waits a second to retry */
        establishSession(100);
        Thread.yield();
        provider.check(object1);
        endConnection();
        sender.shutdown();
    }
    /**    
    
    public void testClosing() throws Exception {
        ncor.close();
        provider.checkClosed();
        Thread.yield();
        provider.printRules();
        Thread [] threads = new Thread[Thread.activeCount()];
        int threadCt = Thread.enumerate(threads);
        for (int i=0; i<threadCt; i++) {
            if (threads[i].getName().equals(threadName)) {
                assertFalse(threads[i].isAlive());
            }
        }
    }
*/
    private class SimpleNetworkMock implements Network {

        private ObjectReceiver mockProvider; 
        public void startService(Service service, int port) throws IOException {
            // TODO Auto-generated method stub
            
        }

        public void stopService(int port) throws IOException {
            // TODO Auto-generated method stub
            
        }

        public synchronized ObjectReceiver findServer(String ipAddress, int port, ObjectReceiver client) throws IOException {
            try {
                wait();
            } catch (InterruptedException unexpected) {}
            return mockProvider;
        }
        
        public void setProvider(ObjectReceiver mock) {
            mockProvider = mock;
        }
        
        public synchronized void releaseServer() {
            notify();
        }
        
    }

    private class MockClientSender extends Thread {
        private boolean shutdownRequested = false;
        
        private List sendQ;
        
        private ObjectReceiver scri;
        
        public MockClientSender (ObjectReceiver scri) {
            this.scri = scri;
            List sendList = new ArrayList();
            sendQ = Collections.synchronizedList(sendList);
            this.setName("MockClientSender");
            this.setDaemon(true);
            start();
        }
        
        public void run() {
            while (!shutdownRequested) {
                sendAnObject();
            }
        }
        
        private synchronized void sendAnObject() {
            if (sendQ.isEmpty()) {
                try {
                    wait();
                    if (shutdownRequested) {
                        return;
                    }
                    Object o = sendQ.remove(0);
                    this.scri.receive(o);
                } catch (InterruptedException uhOh) {
                } catch (IOException cantHappen) {
                    throw new RuntimeException("Stubborn Client threw Exeption");
            
                }
            }
        }
        
        public synchronized void shutdown() {
            shutdownRequested = true;
            notify();
        }
        
        public synchronized void send (Object o) {
            sendQ.add(o);
            notify();
        }
    }

}