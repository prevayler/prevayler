package org.prevayler.foundation.network;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;


public class NewNetworkMock extends BaseNetworkMock
                            implements Network, NetworkReceiverFactory {

    private final Map _providerByPort = new Hashtable();
    /* (non-Javadoc)
     * @see org.prevayler.foundation.network.Network#startService(org.prevayler.foundation.network.Service, int)
     */
    public void startService(Service service, int port) throws IOException {
        checkNotInUse(port);
        ObjectServerSocket server = startServer(port);
        _providerByPort.put(new Integer(port),
                            new NetworkServerObjectReceiverImpl(this, service, server));

    }

    /* (non-Javadoc)
     * @see org.prevayler.foundation.network.Network#stopService(int)
     */
    public void stopService(int port) throws IOException {
        NetworkServerObjectReceiverImpl provider = removeServer(port); 
        if (provider == null) {
            throw new IOException("illegal port number used");
        }
        provider.shutdown();
    }

    /* (non-Javadoc)
     * @see org.prevayler.foundation.network.Network#findServer(java.lang.String, int, org.prevayler.foundation.network.ObjectReceiver)
     */
    public ObjectReceiver findServer(String ipAddress, int port,
            ObjectReceiver client) throws IOException {
        crashIfNotLocal(ipAddress);
        ObjectSocket provider = startClient(port);
        return new NetworkClientObjectReceiverImpl(provider, client);
    }
    private void checkNotInUse(int serverPort) throws IOException  {
        Object old = _providerByPort.get(new Integer(serverPort));
        if (old != null) throw new IOException("Port already in use.");
    }
    private NetworkServerObjectReceiverImpl removeServer(int serverPort) {
        _serverSocketByPort.remove(new Integer(serverPort));
        return (NetworkServerObjectReceiverImpl) _providerByPort.remove(new Integer(serverPort));

    }

    public ObjectReceiver newReceiver(Service service, ObjectSocket socket) throws IOException {
        return new NetworkClientObjectReceiverImpl(socket, service);
    }

}
