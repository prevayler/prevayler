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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Stubborn Network keeps reconnecting sockets should they fail.
 * 
 * Lets UnknownHostException pass back to caller (not much can do if the
 * hostname / IP Address is wrong).
 * 
 */
public class StubbornNetworkImpl extends BaseNetworkImpl implements StubbornNetwork {

    private final Map<Service, SessionsManager> _sessionsInService;

    public StubbornNetworkImpl() {
        super();
        _sessionsInService = Collections.synchronizedMap(new HashMap<Service, SessionsManager>());
    }

    @Override public ObjectReceiver newReceiver(String ipAddress, int port, ObjectReceiver client) {
        return new StubbornClientReceiverImpl(this, ipAddress, port, client);
    }

    @Override public ObjectReceiver newReceiver(Service service, ObjectSocket socket) {
        SessionsManager sessionsManager = getSessionManager(service);
        return new StubbornServerReceiverImpl(service, socket, sessionsManager);
    }

    private SessionsManager getSessionManager(Service service) {
        SessionsManager sessionsManager;
        sessionsManager = _sessionsInService.get(service);
        if (sessionsManager == null) {
            sessionsManager = new SessionsManagerImpl();
            _sessionsInService.put(service, sessionsManager);
        }
        return sessionsManager;
    }

    public ObjectSocket newInstance(String ipAddress, int port) throws IOException {
        return new ObjectSocketImpl(ipAddress, port);
    }

}
