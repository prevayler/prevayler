package org.prevayler.socketserver.server;

import java.io.Serializable;

/**
 * This class is sent to the server in order to request callback messages of a certain type
 * @author djo
 */
public class RegisterCallback implements Serializable {
    public RegisterCallback(String m) { message = m; }
    public String message;
}
