/*
 * NetworkProxyTest.java
 *
 */
package org.prevayler.foundation.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;


public class NetworkProxyTest extends TestCase {

    private StubbornNetworkProxy proxy;
    private ObjectReceiverMock client;
    private ObjectReceiverMock network;
    private ObjectReceiverMock network2;
    private MockClientSender sender;
    private MockInboundChannel receiver1;
    private MockInboundChannel receiver2;
    
    private static final String a = "A";
    private static final String b = "B";
    
    public void setUp() {
        proxy = new StubbornNetworkProxy();
        client = new ObjectReceiverMock();
        proxy.setClient(client);
        network = new ObjectReceiverMock();
        network2 = new ObjectReceiverMock();
        receiver1 = new MockInboundChannel();
        receiver2 = new MockInboundChannel();
        
        sender = new MockClientSender(proxy);
    }
    public void testConnection() throws Exception{
        proxy.connect(network, receiver1);
        proxy.receive(a);
        assertSame(a,network.selfCheck());
        assertFalse(network.isClosed());
    }
    
    public void testDisconnection() throws Exception{
        Thread.yield();
        testConnection();
        proxy.disconnect();
        sender.send(b);
        assertTrue(network.checkEmpty());
        proxy.connect(network2, receiver2);
        assertSame(b,network2.selfCheck());
        assertFalse(network2.isClosed());
    }

    
    
    public void testCloseWorking() throws Exception {
        Thread.yield();
        testConnection();
        proxy.close();
        assertTrue(network.isClosed());
        assertNull(proxy.getClient());
        assertTrue(receiver1.disconnected);
    }
    
    public void testCloseFailing() throws Exception {
        Thread.yield();
        testConnection();
        network.receiveCrash();
        proxy.close();
        assertTrue(network.isClosed());
        assertNull(proxy.getClient());
        assertTrue(receiver1.disconnected);
        
    }
    
    private class MockInboundChannel implements StubbornNetworkClientConnector {

        private boolean disconnected;
        
        public void disconnect() {
              disconnected = true;
        }
        
        public boolean getDisconnected() {
            return disconnected;
        }
    }
    private class MockClientSender extends Thread {
        private boolean shutdownRequested = false;
        
        private List sendQ;
        
        private ObjectReceiver targetReceiver;
        
        public MockClientSender (ObjectReceiver or) {
            this.targetReceiver = or;
            sendQ = Collections.synchronizedList(new ArrayList());
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
                    this.targetReceiver.receive(o);
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
