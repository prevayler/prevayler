//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the license distributed along with this file for more details.
//Contributions: anon.
package org.prevayler.foundation.network;

import java.io.IOException;

import org.prevayler.foundation.Cool;


public class ObjectReceiverMock implements ObjectReceiver {

    private Object received = null;
    
    private boolean closed = false;

    public void receive(Object object) throws IOException {
        received = object;
    }

    public void close() throws IOException {
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
    
    public boolean check(Object expected) {
        while (this.received == null) {
            Cool.sleep(2);
        }
        return (expected.equals(this.received));
    }
}
