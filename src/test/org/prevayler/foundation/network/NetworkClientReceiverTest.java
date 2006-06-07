/*
 * NetworkClientReceiverTest.java
 *
 * Copyright (c) 2005 MoneySwitch Ltd.
 * Level 5, 55 Lavender St, Milsons Point 2061.
 * All rights reserved.
 *
 */
package org.prevayler.foundation.network;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;


/**
 * Useful class comments should go here
 *
 * $Revision: 1.2 $
 * $Date: 2005/03/02 06:04:08 $
 * $Author: peter_mxgroup $
 */
public class NetworkClientReceiverTest extends TestCase {

    MockObjectSocket provider;
    ObjectReceiverMock client;
    NetworkClientObjectReceiverImpl ncor;
    private static final String threadName = "Prevayler Network Client Receiver";
    
    public void setUp() throws Exception {
        provider = new MockObjectSocket();
        client   = new ObjectReceiverMock();
        ncor     = new NetworkClientObjectReceiverImpl(provider, client);
        Thread.yield();
    }
    
    public void testGoodReceiving() throws Exception {
        String object1 = "A";
        provider.addToReceiveQueue(object1);
        client.check(object1);
        ncor.close();
        Thread.yield();
        provider.printRules();
    }
    
    public void testReceiveIOException() throws Exception {
        provider.crash();
        Thread.yield();
        assertTrue(client.isClosed());
        provider.checkClosed();
        Thread.yield();
        checkThreadGone();
        Thread.yield();
        provider.printRules();
    }
    

    public void testSending() throws Exception {
        String object1 = "A";
        ncor.receive(object1);
        provider.check(object1);
        ncor.close();
        Thread.yield();
        provider.printRules();
    }
    
    public void testSendIOException() throws Exception {
        provider.sendCrash();
        String object1 = "A";
        try {
            ncor.receive(object1);
            fail("Should've thrown IO Exception");
        } catch (IOException expected) {}
        Thread.yield();
        ncor.close();
        Thread.yield();
        provider.printRules();
    }
    
    public void testClosing() throws Exception {
        ncor.close();
        provider.checkClosed();
        Thread.yield();
        provider.printRules();
        checkThreadGone();
    }
 
    private void checkThreadGone() {
        Thread [] threads = new Thread[Thread.activeCount()];
        int threadCt = Thread.enumerate(threads);
        for (int i=0; i<threadCt; i++) {
            if (threads[i].getName().equals(threadName)) {
                assertFalse(threads[i].isAlive());
            }
        }
    }

    private class MockObjectSocket implements ObjectSocket {
        
        private ArrayList receiveQueue = new ArrayList();
        
        private ArrayList sentQueue = new ArrayList();

        private boolean closed = false;
        private boolean permit = true;
        private boolean print;
        
        private String monitoringRules = "";
        
        public MockObjectSocket() {
            this(false);
        }
        
        public MockObjectSocket(boolean print) {
            this.print = print;
        }

        /* (non-Javadoc)
         * @see org.prevayler.foundation.network.ObjectSocket#writeObject(java.lang.Object)
         */
        public void writeObject(Object object) throws IOException {
            if (!permit) {
                throw new IOException("Send Crash");
            }
            sentQueue.add(object);
        }

        /* (non-Javadoc)
         * @see org.prevayler.foundation.network.ObjectSocket#readObject()
         */
        public synchronized Object readObject() throws IOException, ClassNotFoundException {
            while (receiveQueue.isEmpty()) {
                try {
                    monitoringRules = monitoringRules + ";Read Waiting";
                    wait();
                    monitoringRules = monitoringRules + ";Read Interrupted";
                    if (closed) {
                        throw new IOException("Close Received");
                    }
                    if (!permit) {
                        throw new IOException("Crash");
                    }
                } catch (InterruptedException expected) {
                }
            }
            return receiveQueue.remove(0);
        }

        
        /* (non-Javadoc)
         * @see org.prevayler.foundation.network.ObjectSocket#close()
         */
        public synchronized void close() throws IOException {
            closed = true;
            monitoringRules = monitoringRules + ";close notify";
            notify();
        }
        
        public synchronized void addToReceiveQueue(Object o) {
            receiveQueue.add(o);
            monitoringRules = monitoringRules + ";read notify";
            notify();
        }
        
        public synchronized void check(Object o) {
            while (sentQueue.isEmpty()) {
                try {
                    monitoringRules = monitoringRules + ";check waiting";
                    wait();
                    monitoringRules = monitoringRules + ";check interrupted";
                } catch (InterruptedException expected) {}
             }
            assertSame(o,sentQueue.remove(0));
        }
        
        public void checkClosed() {
            assertTrue(closed);
        }
        public void printRules() {
            if (print) {
                System.out.println(monitoringRules);
            }
        }
        
        public synchronized void crash() {
            permit = false;
            notify();
        }
        
        public synchronized void sendCrash() {
            permit = false;
        }
    }
}
