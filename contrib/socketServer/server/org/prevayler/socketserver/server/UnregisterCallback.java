package org.prevayler.socketserver.server;

import java.io.Serializable;

/**
 * This class is sent to the server in order to request no more callback messages of a certain type
 * @author djo
 */
public class UnregisterCallback implements Serializable {
    public UnregisterCallback(String m) { message = m; }
    public String message;
}
