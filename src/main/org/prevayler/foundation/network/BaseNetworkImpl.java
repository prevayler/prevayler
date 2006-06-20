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
 * Useful class comments should go here
 * 
 */
public abstract class BaseNetworkImpl implements Network, NetworkReceiverFactory {
    private final Map<Integer, NetworkServerObjectReceiver> _servicesInUse;

    public BaseNetworkImpl() {
        _servicesInUse = Collections.synchronizedMap(new HashMap<Integer, NetworkServerObjectReceiver>());
    }

    public void startService(Service service, int port) throws IOException {
        checkPortNotUsed(port);
        NetworkServerObjectReceiver sessionServices = createService(service, port);
        markPortAsUsed(port, sessionServices);
    }

    public void stopService(int port) throws IOException {
        try {
            NetworkServerObjectReceiver stubbornService = locateService(port);
            terminateService(stubbornService);
        } finally {
            releasePortForReuse(port);
        }
    }

    public ObjectReceiver findServer(String ipAddress, int port, ObjectReceiver client) throws IOException {
        return newReceiver(ipAddress, port, client);
    }

    private void markPortAsUsed(int port, NetworkServerObjectReceiver sessionService) {
        _servicesInUse.put(new Integer(port), sessionService);
    }

    private void checkPortNotUsed(int port) throws IOException {
        if (_servicesInUse.get(new Integer(port)) != null) {
            throw new PortInServiceException("Port In Service");
        }
    }

    private void releasePortForReuse(int port) {
        _servicesInUse.remove(new Integer(port));
    }

    protected NetworkServerObjectReceiver createService(Service service, int port) throws IOException {
        return new NetworkServerObjectReceiverImpl(this, service, port);
    }

    private void terminateService(NetworkServerObjectReceiver stubbornService) {
        stubbornService.shutdown();
    }

    private NetworkServerObjectReceiver locateService(int port) throws IOException {
        NetworkServerObjectReceiver service = _servicesInUse.get(new Integer(port));
        if (service == null) {
            throw new PortNotInServiceException("Port Not in Service");
        }
        return service;
    }

    public abstract ObjectReceiver newReceiver(Service service, ObjectSocket socket) throws IOException;

    public abstract ObjectReceiver newReceiver(String ipAddress, int port, ObjectReceiver client) throws IOException;
}
