//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of 
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
//See the license distributed along with this file for more details.
package org.prevayler.foundation.network;

import java.io.IOException;
import java.util.Hashtable;


/**
 * Stubborn Network keeps reconnecting sockets should they fail.
 * 
 * Lets UnknownHostException pass back to caller (not much can do if the 
 * hostname / IP Address is wrong).
 *
 * $Revision: 1.1 $
 * $Date: 2005/02/16 04:28:34 $
 * $Author: peter_mxgroup $
 */
public class StubbornNetwork implements Network {

    /** the network connection */
    private final Network _unreliableDelegate;
    
    private final Hashtable _servicesInUse = new Hashtable();
    

    /**
     * Constructor
     * @param unreliableNetwork - the network that does the work
     */
    public StubbornNetwork(Network unreliableNetwork) {
       _unreliableDelegate = unreliableNetwork;
    }

    public void startService(Service service, int port) throws IOException {
        checkPortNotUsed(port);
        Service stubbornService = createService(service, port);
        markPortAsUsed(port, stubbornService);
    }


    public void stopService(int port) throws IOException {
        try {
            Service stubbornService = locateService(port);
            terminateService(stubbornService);
        } finally {
            releasePortForReuse(port);
        }
    }


    public ObjectReceiver findServer(String ipAddress, int port, ObjectReceiver client) throws IOException {
        return new StubbornClientReceiverImpl(_unreliableDelegate,
                                          ipAddress, port, client);
    }

    
    
    
    
    private void markPortAsUsed(int port, Service stubbornService) {
        _servicesInUse.put(new Integer(port),stubbornService);
    }

    private Service createService(Service service, int port) {
        return new StubbornServiceImpl(_unreliableDelegate, service, port);
    }

    private void checkPortNotUsed(int port) throws IOException {
        if (_servicesInUse.get(new Integer(port)) != null) {
            throw new IOException("Port In Service");
        }
    }
    private void releasePortForReuse(int port) {
        _servicesInUse.remove(new Integer(port));
    }

    private void terminateService(Service stubbornService) {
        ((StubbornServiceImpl)stubbornService).shutdown();
    }

    private Service locateService(int port) throws IOException{
        Service service = (Service) _servicesInUse.get(new Integer(port));
        if (service == null) {
            throw new IOException("Port Not in Service");
        }
        return service;
    }

}
