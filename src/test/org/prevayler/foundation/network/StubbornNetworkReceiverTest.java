package org.prevayler.foundation.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.prevayler.foundation.Cool;

import junit.framework.TestCase;


public class StubbornNetworkReceiverTest extends TestCase {

    private MockObjectSocket provider;
    private ObjectReceiverMock client;
    private SimpleNetworkMock network;
    private StubbornClientReceiverImpl scri;
    private final NetworkSessionId id0 = new NetworkSessionId(0,0);
    private final String replacementProvider = "replaced provider";

    public void setUp () throws Exception {
//        System.out.println("Starting " + this.getName());
        client   = new ObjectReceiverMock();
        network  = new SimpleNetworkMock();
        provider = prepareNetwork("Initial Provider");
        Thread.yield();
    }
    
    private MockObjectSocket prepareNetwork(String providerName) {
        MockObjectSocket tempProvider = new MockObjectSocket(providerName);
        network.setProvider(tempProvider);
        return tempProvider;
    }
    
    private void endConnection() throws Exception {
        scri.close();
        Thread.yield();
        assertTrue(provider.isClosed());
        
    }
    
    private void checkForSessionId(int id) {
        Object received = provider.selfCheck();
        if (received instanceof NetworkSessionId) {
            assertEquals(new NetworkSessionId(0,id),received);
        } else {
            fail ("Did not receive the Session Id");
        }
    }

    private void startSession (int sessionId) {
        Thread.yield();
        checkForSessionId(sessionId);
    }
    
    private void completeSession (int sessionId) throws Exception {
        provider.read(new NetworkSessionId(0,sessionId));
        Thread.yield();
    }
    
    private void establishSession(int initialSessionId) throws Exception{
        startSession(initialSessionId);
        completeSession(100);
    }

    public void testSessionStart() throws Exception {
        scri = new StubbornClientReceiverImpl(network, "localhost", 9876, client);
        startSession(0);
        endConnection();
    }
    
    public void testSessionAbort() throws Exception {
        provider.failWriteToNetwork();
        scri = new StubbornClientReceiverImpl(network, "localhost", 9876, client);
        Thread.yield();
        assertTrue(provider.isClosed());
        provider = prepareNetwork(replacementProvider);
        Thread.sleep(1100); /* the client waits a second to retry */
        startSession(0);
        endConnection();
    }
    
    public void testSessionFailResponse() throws Exception {
        scri = new StubbornClientReceiverImpl(network, "localhost", 9876, client);
        startSession(0);
        provider.failReadFromNetwork();
        Thread.yield();
        assertTrue(provider.isClosed());
        provider = prepareNetwork(replacementProvider);
        Thread.sleep(1100);
        startSession(0);
        endConnection();
    }
    
    public void testFailAfterSessionEstablished() throws Exception {
        scri = new StubbornClientReceiverImpl(network, "localhost", 9876, client);
        establishSession(0);
        Thread.yield();
        provider.failReadFromNetwork();
        Thread.yield();
        assertTrue(provider.isClosed());
        provider = prepareNetwork(replacementProvider);
        Thread.sleep(1100);
        checkForSessionId(100);
        endConnection();
    }
    public void testGoodReceiving() throws Exception {
        scri = new StubbornClientReceiverImpl(network, "localhost", 9876, client);
        establishSession(0);
        String object1 = "A";
        provider.read(object1);
        client.check(object1);
        endConnection();
    }
    
    

    public void testSending() throws Exception {
        scri = new StubbornClientReceiverImpl(network, "localhost", 9876, client);
        establishSession(0);
        String object1 = "A";
        scri.receive(object1);
        provider.check(object1);
        endConnection();
    }
    
    
    public void testSendIOException() throws Exception {
        scri = new StubbornClientReceiverImpl(network, "localhost", 9876, client);
        Thread.yield();
        MockClientSender sender = new MockClientSender(scri);
        MockObjectSocket provider2 = prepareNetwork("Provider 2");
        Thread.yield();
        establishSession(0);
        provider.failWriteToNetwork();
        String object1 = "A";
        sender.send(object1);
        Thread.yield();
        assertTrue(provider.checkEmpty());
        assertTrue(provider.isClosed());
        Thread.sleep(1100); /* the client waits a second to retry */
        provider = provider2;
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
    private class SimpleNetworkMock implements StubbornNetwork {

        private MockObjectSocket mockProvider; 

        public ObjectSocket newInstance(String ipAddress, int port) throws IOException {
            return mockProvider;
        }
        
        public void setProvider(MockObjectSocket mock) {
            mockProvider = mock;
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

    private class MockObjectSocket implements ObjectSocket {
        private List received;
        private List readQ;
        
        private volatile boolean closed = false;
        private Permit permitRead  = new Permit();
        private Permit permitWrite = new Permit();
        private Permit permitClose = new Permit();
        
        private boolean shutdown = false;
        private final String _name; 
        

        MockObjectSocket(String name) {
            received = Collections.synchronizedList(new ArrayList());
            readQ    = Collections.synchronizedList(new ArrayList());
            _name = name;
        }
 
        public String getName() {
            return _name;
        }
        public synchronized void failReadFromNetwork() {
            permitRead.clear();
            notify();
        }
        
        public synchronized void failWriteToNetwork() {
            permitWrite.clear();
        }
        
        public synchronized void failClose() {
            permitClose.clear();
        }

        public void checkPermission(Permit permit) throws IOException {
            if (!permit.check()) {
                 throw new IOException("network failure");
            }
        }
        /* (non-Javadoc)
         * @see org.prevayler.foundation.network.ObjectSocket#writeObject(java.lang.Object)
         */
        public void writeObject(Object object) throws IOException {
            checkPermission(permitWrite);
            received.add(object);
        }

        /* (non-Javadoc)
         * @see org.prevayler.foundation.network.ObjectSocket#readObject()
         */
        public Object readObject() throws IOException, ClassNotFoundException {
            checkPermission(permitRead);
            return readAnObject();
        }
        private synchronized Object readAnObject() throws IOException {
            while (readQ.isEmpty()) {
                try {
                    wait();
                    if (closed) {
                        throw new IOException("Socket CLosed");
                    }
                    checkPermission(permitRead);
                } catch (InterruptedException uhOh) {
                    throw new RuntimeException(uhOh);
                }
            }
            Object o = readQ.remove(0);
            return o;
        }
        
        public synchronized void read (Object o) {
            readQ.add(o);
            notify();
        }

        public synchronized void close() throws IOException {
            closed = true;
            notify();
            checkPermission(permitClose);
        }

        public boolean isClosed() {
            return closed;
        }
        
        public boolean check(Object expected) {
            while (this.received.isEmpty()) {
                Cool.sleep(2);
            }
            return (expected.equals(this.received.remove(0)));
        }
        
        public Object selfCheck() {
            while (this.received.isEmpty()) {
                Thread.yield();
            }
            return received.remove(0);
        }


        public boolean checkEmpty() {
            return received.isEmpty();
        }
        
        private class Permit {
            private volatile boolean state = true;
            
            public synchronized boolean check() {
                if (state) {
                    return true;
                }
                state = true;
                return false;
            }
            
            public synchronized void clear() {
                state = false;
            }
        }
        
    }
}