/*
 * StubbornNetworkReliabiltyTest.java
 *
 *
 */
package org.prevayler.foundation;

import java.io.IOException;

import junit.framework.TestCase;

import org.prevayler.foundation.network.Network;
import org.prevayler.foundation.network.NetworkImpl;
import org.prevayler.foundation.network.ObjectReceiver;
import org.prevayler.foundation.network.Service;
import org.prevayler.foundation.network.StubbornNetworkImpl;


/**
 * Test the reliabilty of the Stubborn Network. 
 * 
 * This test creates 2 "mock instances of prevayler". 1 acting
 * as a primary prevayler, and the other acting as a slave 
 * (replicator).
 * 
 * It sits on top of a Network Proxy Implementation that can break 
 * and recover.
 */
public class StubbornNetworkReliabiltyTest extends TestCase {

    private MockService serverService;
    
    private Network serverNetwork;
    private Network clientNetwork1;
    private Network clientNetwork2;
    private ClientReceiver client1Receiver;
    private ClientReceiver client2Receiver;
    private NetworkProxy networkProxy;
    private final boolean printing = true; 
    
    public void setUp() {
        serverService    = new MockService();
//        NewNetworkMock mockNetwork = new NewNetworkMock();
        serverNetwork  = new StubbornNetworkImpl();
        clientNetwork1 = new StubbornNetworkImpl();
        clientNetwork2 = new StubbornNetworkImpl();
        networkProxy   = new NetworkProxy();
        client1Receiver = new ClientReceiver("client1");
        client2Receiver = new ClientReceiver("client2");
    }

    public void testNormal() throws Exception {
        String testSequence = "ABCDEFGHIJKL";
        int serverPort = 8765;
        int proxyPort = 5678;
        networkProxy.startProxy(proxyPort, serverPort);
        serverService.setObjectsToSend(testSequence);
        serverService.setFrequency(100);
        serverService.commenceService(serverNetwork, serverPort);
        client1Receiver.commenceReceiving(clientNetwork1, proxyPort, testSequence.length());
        client2Receiver.commenceReceiving(clientNetwork2, proxyPort, testSequence.length());
        //while ((client1Receiver.getState() != Thread.State.TERMINATED) ||
        //      (client2Receiver.getState() != Thread.State.TERMINATED)) {
            Thread.sleep(1000);
        //}
        client1Receiver.checkReceived(testSequence);
        client2Receiver.checkReceived(testSequence);
        serverService.closeDown();
    }
    public class MockService implements Service {
        private String objectsToSend;
        private int frequency;
        private Network network;
        private int port;
        
        
        public void setObjectsToSend (String objectsToSend) {
            this.objectsToSend = objectsToSend;
        }
        
        public void setFrequency(int millisecs) {
            this.frequency = millisecs;
        }

        public void commenceService(Network network, int port) throws Exception {
            this.network = network;
            this.port = port;
            network.startService(this, port);
        }

        public ObjectReceiver serverFor(ObjectReceiver client) {
            return new MockObjectReceiver(objectsToSend, frequency, client);
        }
        
        public void closeDown() throws Exception {
            network.stopService(port);
        }
        
    }
    
    public class MockObjectReceiver extends Thread implements ObjectReceiver {

        private final String objectsToSend;
        private final int frequency;
        private final ObjectReceiver networkClient;
        private int sent = 0;
        private boolean openForBusiness;
        MockObjectReceiver (String objectsToSend, int frequency, ObjectReceiver networkClient) {
            this.objectsToSend = objectsToSend;
            this.frequency = frequency;
            this.networkClient = networkClient;
            this.setName("Server Service Thread");
            start();
        }
        
        public void run () {
            openForBusiness = true;
            for (sent = 0; sent < objectsToSend.length(); sent++) {
                rest();
                send();
            }
            openForBusiness = false;
        }
        
        private void rest() {
            try {sleep(frequency);}
            catch (InterruptedException unEx) {};
        }
        
        private void send() {
            try {
                networkClient.receive(objectsToSend.substring(sent, sent+1));
            } catch (IOException unex) {
                fail("Server received an IOException");
            }
        }
        public void receive(Object object) throws IOException {
            fail("receive called on server");
        }

        /* (non-Javadoc)
         * @see org.prevayler.foundation.network.ObjectReceiver#close()
         */
        public void close() throws IOException {
            if (openForBusiness) {
                fail("close called unexpectedly");
            }
        }
        
    }

    public class ClientReceiver extends Thread implements ObjectReceiver {
        private StringBuffer received;
        
        private int expectedLth;
        private Network network;
        private int port;
        private ObjectReceiver networkProvider;
        private String threadName;
        
        ClientReceiver (String threadName) {
            received = new StringBuffer();
            this.threadName = threadName;
        }
        
        public void checkReceived (String sent) {
            assertEquals("Received " + received + " Expected " + sent,
                          sent.length(), received.length());
            for (int i = 0; i < sent.length(); i++) {
                assertEquals("Received " + received + " Expected " + sent,
                        sent.charAt(i),received.charAt(i));
            }
        }
        
        public void commenceReceiving(Network network, int port, int expectedLth) {
            this.expectedLth = expectedLth;
            this.port = port;
            this.network = network;
            this.setName(threadName);
            this.setDaemon(true);
            start();
        }

        public void run() {
            connect();
            sleepTillFinished();
            shutdown();
        }
        
        private void connect() {
            try {
                networkProvider = network.findServer("localhost", port, this);
            } catch (IOException unex) {
                fail("Client received an IO Exception " + unex);
            }
        }
 
        private void shutdown() {
            try {
                networkProvider.close();
            } catch (IOException unex) {
                fail("Client received an Exception on close " + unex);
            }
        }
        private synchronized void sleepTillFinished() {
            try {
                wait();
            } catch (InterruptedException expected) {
            }
        }
        public void receive(Object object) throws IOException {
            if (object instanceof String) {
                String s = (String) object;
                received.append(s);
                if (printing) {
                    System.out.println(this.getName() + " received " + s);
                }
                if (received.length() >= expectedLth) {
                    finished();
                }
            } else {
                fail("Unexpected object received " + object);
            }
        }

        private synchronized void finished() {
            notify();
        }
        
        public void close() throws IOException {
            
        }
    }

    public class NetworkProxy implements Service {
        
        private Network network;
        
        private int targetPort;
        
        NetworkProxy() {
            network = new NetworkImpl();
        }
        
        public void startProxy (int listeningPort, int targetPort) throws IOException {
            this.targetPort = targetPort;
            network.startService(this, listeningPort);
        }

        /* (non-Javadoc)
         * @see org.prevayler.foundation.network.Service#serverFor(org.prevayler.foundation.network.ObjectReceiver)
         */
        public ObjectReceiver serverFor(ObjectReceiver client) {
            return new ProxyReceiver(network, targetPort, client);
        }
    }

    public class ProxyReceiver extends Thread implements ObjectReceiver {

        private Network network;
        private ObjectReceiver proxyServer;
        private int port;
        private ObjectReceiver proxyClient;
        
        ProxyReceiver (Network network, int port, ObjectReceiver client) {
            this.network = network;
            this.port = port;
            this.proxyServer = client;
            start();
            waitConnected();
        }
        
        private synchronized void waitConnected() {
            try {
                wait();
            } catch (InterruptedException unex) {
                throw new RuntimeException(unex);
            }
        }
        public void run() {
            connect();
            finishConnect();
            monitorMessages();
        }
        
        private synchronized void finishConnect() {
            notify();
        }

        private synchronized void monitorMessages() {
            while (true) {
                try {
                    wait();
                } catch (InterruptedException ex) {}
            }
        }
        
        public void connect() {
            try {
                proxyClient = network.findServer("localhost",port,new MockClient(this));
            } catch (IOException unex) {
                throw new RuntimeException(unex);
            }
        }
        public void receive(Object object) throws IOException {
            messageFromClientForServer(object);
            
        }

        public void close() throws IOException {
            // TODO Auto-generated method stub
            
        }
        
        public void messageFromServerForClient(Object object) {
            try {
                proxyServer.receive(object);
            } catch (IOException ioex) {
                
            }
        }
        
        public void messageFromClientForServer(Object object) {
            try {
                proxyClient.receive(object);
            } catch (IOException ioex) {
                
            }
        }
    }

    public class MockClient implements ObjectReceiver {
        
        private ProxyReceiver proxy;

        MockClient(ProxyReceiver proxy) {
            this.proxy = proxy;
        }
        
        public void receive(Object object) throws IOException {
            proxy.messageFromServerForClient(object);
        }
        public void close() throws IOException {
        }
        
    }
}
