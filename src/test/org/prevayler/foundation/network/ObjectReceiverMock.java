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

import org.prevayler.foundation.Cool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectReceiverMock implements ObjectReceiver {

    private List received;

    private boolean closed = false;

    private boolean permit = true;

    public ObjectReceiverMock() {
        List receiveList = new ArrayList();
        received = Collections.synchronizedList(receiveList);
    }

    public void receive(Object object) throws IOException {
        if (!permit) {
            permit = true;
            throw new IOException("network failure");
        }
        received.add(object);
    }

    public void close() throws IOException {
        closed = true;
        if (!permit) {
            permit = true;
            throw new IOException("network failure");
        }
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

    public void receiveCrash() {
        permit = false;
    }

    public boolean checkEmpty() {
        return received.isEmpty();
    }
}
