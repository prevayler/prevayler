//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful,
//   but WITHOUT ANY WARRANTY; without even the implied warranty of 
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//   See the license distributed along with this file for more details.
//Contributions: anon.

package org.prevayler.foundation;

import java.io.IOException;

import junit.framework.TestCase;

import org.prevayler.foundation.network.Network;
import org.prevayler.foundation.network.NetworkImpl;
import org.prevayler.foundation.network.NewNetworkMock;
import org.prevayler.foundation.network.ObjectReceiver;
import org.prevayler.foundation.network.ObjectReceiverMock;
import org.prevayler.foundation.network.ServiceMock;
import org.prevayler.foundation.network.StubbornNetwork;


/**
 * To test the Network Implementation set mockTest = false 
 * To test with a mockNetwork set mockTest = true;
 */
public class StubbornNetworkTest extends TestCase {
    
    private boolean mockTest = false;

    private Network network;
    
    private int port = 4000;
    
    private ServiceMock mockService;
    
    private Client client1;
    
    private Client client2;
    
    private static int NETWORK_MSG_MSEC_DELAY = 10;
    private static int NETWORK_CONNECT_SETUP_MSEC_TIME = 12;
    private static int NETWORK_SOCKET_CLOSE_MSEC_DELAY = 15;
    
    
    private String testObject1 = "test Object 1";
    private String testObject2 = "test Object 2";
    
    public void setUp () {
        Network oldNetwork = setNetworkToTest();
        network = new StubbornNetwork(oldNetwork);
        mockService = new ServiceMock();
        client1 = new Client();
        client2 = new Client();
    }
    
    public void tearDown () throws Exception {
        Thread.sleep(NETWORK_SOCKET_CLOSE_MSEC_DELAY);
        
    }

    private Network setNetworkToTest() {
        if (mockTest) {
            return  new NewNetworkMock();
        }
        return new NetworkImpl();
    }
    
    
    public void testMessageSendBothWays() throws Exception {
        network.startService(mockService,port);
        client1.connect(port);
        Server server = new Server(1, mockService);
        server.send(testObject1);
        client1.received(testObject1);
        client1.send(testObject2);
        server.received(testObject2);
        server.close();
        client1.close();
        network.stopService(port);
    }
    
    public synchronized void test2Clients() throws Exception {
//        port++; 
        network.startService(mockService,port);
        client1.connect(port);
        client2.connect(port);
        Server server2 = new Server(2,mockService);
        Server server1 = new Server(1,mockService);
        try {
            this.wait(100);
        } catch (InterruptedException ex) {
            
        }
        sendTheMessages(server1, server2);
        network.stopService(port);
    }
    public void testDuplicateStartCaught() throws Exception {
        network.startService(mockService, port);
        try {
            network.startService(mockService, port);
            fail ("IOException for duplicate port not thrown");
        } catch (IOException expected) {}
       network.stopService(port);
    }
    
    public void testCloseUnopenedService() {
        try {
            network.stopService(port);
            fail("IOException for unused port not thrown");
        } catch (IOException expected) {};
    }
    
    public void test2Services() throws Exception {
        network.startService(mockService,port);
        ServiceMock mockService2 = new ServiceMock();
        network.startService(mockService2, (port + 1));
        client1.connect(port);
        client2.connect(port+1);
        Server server1 = new Server(1, mockService);
        Server server2 = new Server(1, mockService2);
        sendTheMessages(server1, server2);
        network.stopService(port);
        network.stopService(port + 1);
    }
    
    public void testRestartService() throws Exception {
        testMessageSendBothWays();
        tearDown();
        mockService.reset();
        testMessageSendBothWays();
    }
    
    private void sendTheMessages(Server server1, Server server2) throws Exception {
        server1.send(testObject1);
        server2.send(testObject1);
        client1.received(testObject1);
        client2.received(testObject1);
        server1.send(testObject2);
        server2.send(testObject2);
        client1.received(testObject2);
        client2.received(testObject2);
        client1.close();
        client2.close();
        server1.close();
        server2.close();
    }
    
    class Messenger {
        protected ObjectReceiverMock mock;
        protected ObjectReceiver networkReceiver;

        public void send (Object o) throws Exception {
            networkReceiver.receive(o);
            Thread.sleep(NETWORK_MSG_MSEC_DELAY);
        }
        
        public void received (Object o) {
             assertTrue(mock.check(o));
        }
        
    }
    class Client extends Messenger {
        
        public void connect (int port) throws Exception {
            mock = new ObjectReceiverMock();
            networkReceiver = network.findServer("localhost",port,mock);
            Thread.sleep(NETWORK_CONNECT_SETUP_MSEC_TIME);
        }
        
        public void close () throws Exception {
            networkReceiver.close();
        }
        
            
    }
    
    class Server extends Messenger {
        private ServiceMock mockService;
        private int service;
        
        
        public Server (int service, ServiceMock mockService) {
            this.service = service;
            this.mockService = mockService;
            networkReceiver = this.mockService.getServerNetwork(service);
            mock = this.mockService.getServerMock(service);
        }
        
        public void close() throws Exception {
            this.mockService.close(service);
        }
        
    }
}
