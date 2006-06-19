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

import java.util.HashMap;

/**
 * Manages the sessions for a service
 * 
 */
public class SessionsManagerImpl implements SessionsManager {
    private final long timeStamp;

    private int _masterSessionId = 100;

    private HashMap _sessions = new HashMap();

    public SessionsManagerImpl() {
        this(System.currentTimeMillis());
    }

    protected SessionsManagerImpl(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public synchronized StubbornNetworkProxy find(NetworkSessionId sessionId) {
        return (StubbornNetworkProxy) _sessions.get(sessionId);
    }

    public synchronized NetworkSessionId add(StubbornNetworkProxy receiver) {
        NetworkSessionId key = new NetworkSessionId(timeStamp, _masterSessionId++);
        _sessions.put(key, receiver);
        return key;
    }

    public synchronized void remove(NetworkSessionId sessionId) {
        _sessions.remove(sessionId);
    }

}
