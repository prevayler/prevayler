/*
 * ServiceMock.java
 *
 * Copyright (c) 2004 MoneySwitch Ltd.
 * Level 5, 55 Lavender St, Milsons Point 2061.
 * All rights reserved.
 *
 */
package org.prevayler.foundation.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.prevayler.foundation.Cool;


/**
 * Useful class comments should go here
 *
 * $Revision: 1.2 $
 * $Date: 2005/02/16 04:28:34 $
 * $Author: peter_mxgroup $
 */
public class ServiceMock implements Service {

    
    private Map clients = new HashMap();    // the network receiver
    private Map serverMock = new HashMap(); // the testcode receiver
    private int index = 1;

    public synchronized ObjectReceiver serverFor(ObjectReceiver client) {
        Integer key = new Integer(index);
        ObjectReceiver mock = new ObjectReceiverMock();
        serverMock.put(key, mock);
        clients.put(key, client);
        index++;
        return mock;
    }

    public void close(int service) throws IOException {
            ((ObjectReceiver)clients.remove(new Integer(service))).close();
    }

    
    public ObjectReceiver getServerNetwork (int key) {
        Integer theKey = new Integer(key);
        Object receiver = clients.get(theKey);
        while (receiver == null) {
            Cool.sleep(1);
            receiver = clients.get(theKey);
        }
         return (ObjectReceiver) receiver;
    }
    
    public ObjectReceiverMock getServerMock(int key) {
        return (ObjectReceiverMock) serverMock.get(new Integer(key));
    }
    
    public void reset() {
        index = 1;
    }
}
