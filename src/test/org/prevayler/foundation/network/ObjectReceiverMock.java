//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the license distributed along with this file for more details.
//Contributions: anon.
package org.prevayler.foundation.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.prevayler.foundation.Cool;


public class ObjectReceiverMock implements ObjectReceiver {

    private List received;
    
    private boolean closed = false;

    private boolean permit = true;
    
    public ObjectReceiverMock() {
        List receiveList = new ArrayList();
        received = Collections.synchronizedList(receiveList);
    }
    public void receive(Object object) throws IOException {
        System.out.println("received " + object);
        if (!permit) {
            permit = true;
            throw new IOException("network failure");
        }
        received.add(object);
    }

    public void close() throws IOException {
        closed = true;
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

    /**
     * 
     */
    public void receiveCrash() {
        permit = false;
        
    }
}
