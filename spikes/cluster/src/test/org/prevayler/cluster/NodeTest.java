package org.prevayler.cluster;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.prevayler.cluster.ClusterListener;
import org.prevayler.cluster.Node;

public class NodeTest extends TestCase {
    private Node node;
    private List receivedMessages = new ArrayList();

    protected void setUp() throws Exception {
        super.setUp();
        node = new Node();
        receivedMessages = new ArrayList();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        node.shutdown();
    }

    public void testFindNodes() throws Exception {
        assertEquals(1, node.getNumberOfNodesInCluster());
        Node node2 = createNodeAndWait();
        assertEquals(2, node.getNumberOfNodesInCluster());
        node2.shutdown();
        assertEquals(1, node.getNumberOfNodesInCluster());
    }

    public void testAssignMaster() throws Exception {
        assertEquals(node.getAddress(), node.getMasterAddress());
        Node node2 = createNodeAndWait();
        assertEquals(node.getAddress(), node2.getMasterAddress());
        assertFalse(node.getAddress().equals(node2.getAddress()));
        node.shutdown();
        assertEquals(node2.getAddress(), node2.getMasterAddress());
        node2.shutdown();
    }

    public void testMultipleNodes() throws Exception {
        Node node2 = createNodeAndWait();
        assertEquals(node.getAddress(), node.getMasterAddress());
        assertEquals(node.getAddress(), node2.getMasterAddress());
        node2.shutdown();
    }

    public void testBroadCast() throws Exception {
        Node node2 = createNodeAndWait();
        node2.setListener(createListAddListener());
        String message = "Hello World";
        node.broadcast(message);
        assertEquals(1, receivedMessages.size());
        assertEquals(message, receivedMessages.get(0));
        node2.shutdown();
    }

    public void testMultipleMessages() throws Exception {
        Node node2 = createNodeAndWait();
        node.setListener(createListAddListener());
        int numberOfMessages = 100;
        for (int i = 0; i < numberOfMessages; i++) {
            if (numberOfMessages % 2 == 0) {
                node.broadcast(new Integer(i));
            } else {
                node2.broadcast(new Integer(i));
            }
        }
        node2.shutdown();
        assertEquals(100, receivedMessages.size());
        for (int i = 0; i < numberOfMessages; i++) {
            assertEquals(new Integer(i), receivedMessages.get(i));
        }
    }

    private ClusterListener createListAddListener() {
        return new ClusterListener() {
            public Object receive(Object message) {
                receivedMessages.add(message);
                return null;
            }

            public Object getState() { return null; }
            public void setState(Object state) {}
        };
    }

    private Node createNodeAndWait() throws InterruptedException {
        Node node = new Node();
        node.waitForConnection();
        return node;
    }
}
