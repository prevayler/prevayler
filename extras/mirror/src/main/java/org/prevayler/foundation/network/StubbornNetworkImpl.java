//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of 
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
//See the license distributed along with this file for more details.
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
    private final Map _sessionsInService;

    public StubbornNetworkImpl () {
        super();
        _sessionsInService       = Collections.synchronizedMap(new HashMap());
    }

    public ObjectReceiver newReceiver(String ipAddress, int port, ObjectReceiver client) throws IOException {
        return new StubbornClientReceiverImpl(this, ipAddress, port, client);
    }
    public ObjectReceiver newReceiver(Service service, ObjectSocket socket) throws IOException {
        SessionsManager sessionsManager = getSessionManager(service);
        
        return new StubbornServerReceiverImpl(service, socket, sessionsManager);
    }
    
    private SessionsManager getSessionManager(Service service) {
        SessionsManager sessionsManager;
        sessionsManager = (SessionsManager) _sessionsInService.get(service);
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
