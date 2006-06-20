// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.foundation.network;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

public class NetworkClientReceiverTest extends TestCase {

    MockObjectSocket provider;

    ObjectReceiverMock client;

    NetworkClientObjectReceiverImpl ncor;

    private static final String threadName = "Prevayler Network Client Receiver";

    @Override public void setUp() throws Exception {
        provider = new MockObjectSocket();
        client = new ObjectReceiverMock();
        ncor = new NetworkClientObjectReceiverImpl(provider, client);
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
        } catch (IOException expected) {
        }
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
        Thread[] threads = new Thread[Thread.activeCount()];
        int threadCt = Thread.enumerate(threads);
        for (int i = 0; i < threadCt; i++) {
            if (threads[i].getName().equals(threadName)) {
                assertFalse(threads[i].isAlive());
            }
        }
    }

    private class MockObjectSocket implements ObjectSocket {

        private ArrayList<Object> receiveQueue = new ArrayList<Object>();

        private ArrayList<Object> sentQueue = new ArrayList<Object>();

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

        public void writeObject(Object object) throws IOException {
            if (!permit) {
                throw new IOException("Send Crash");
            }
            sentQueue.add(object);
        }

        public synchronized Object readObject() throws IOException {
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

        public synchronized void close() {
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
                } catch (InterruptedException expected) {
                }
            }
            assertSame(o, sentQueue.remove(0));
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
