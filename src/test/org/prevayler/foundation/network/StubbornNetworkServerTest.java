package org.prevayler.foundation.network;

import java.io.IOException;

import junit.framework.TestCase;


public class StubbornNetworkServerTest extends TestCase {
    private static final long timeStamp = System.currentTimeMillis();
    private ObjectReceiverMock provider;
    private StubbornServerReceiverImpl serverReceiver;
    private SessionsManager sessionsManager;
    private ServiceMock service;
    private static final NetworkSessionId id0   = new NetworkSessionId(timeStamp ,0);
    private static final NetworkSessionId id100 = new NetworkSessionId(timeStamp, 100);

    public void setUp () throws Exception {
        provider = new ObjectReceiverMock();
        sessionsManager = new SessionsManagerImpl(timeStamp);
        service = new ServiceMock();
        serverReceiver = new StubbornServerReceiverImpl(provider, service, sessionsManager);
        Thread.yield();
    }
    
    private void endConnection(NetworkSessionId sessionId) throws Exception {
        serverReceiver.disconnect();
        Thread.yield();
        assertFalse(provider.isClosed());
        assertNull(sessionsManager.find(sessionId));
    }
    
    private void checkForSessionId(NetworkSessionId id) {
        Object received = provider.selfCheck();
        if (received instanceof NetworkSessionId) {
            assertEquals(id, received);
        } else {
            fail ("Did not receive the Session Id");
        }
    }

    private void startSession (NetworkSessionId sessionId) throws Exception{
        serverReceiver.receive(id0);
        checkForSessionId(sessionId);
    }
    
    

    public void testSessionStart() throws Exception {
        startSession(id100);
        assertNotNull(service.getServerMock(1));
        assertNotNull(sessionsManager.find(id100));
        assertEquals(service.getServerMock(1),sessionsManager.find(id100).getClient());
        endConnection(id100);
    }
    
    public void testSessionAbort() throws Exception {
        provider.receiveCrash();
        serverReceiver.receive(id0);
        assertTrue(provider.isClosed());
        assertEquals(service.getServerMock(1),sessionsManager.find(id100).getClient());
    }
    
    
    public void testReceive() throws Exception{
        startSession(id100);
        String object = "B";
        serverReceiver.receive(object);
        assertFalse(provider.isClosed());
        assertEquals(service.getServerMock(1),sessionsManager.find(id100).getClient());
        service.getServerMock(1).check(object);
    }

    public void testSocketDies() throws Exception {
        startSession(id100);
        serverReceiver.close();
        assertEquals(service.getServerMock(1),sessionsManager.find(id100).getClient());
    }
}
