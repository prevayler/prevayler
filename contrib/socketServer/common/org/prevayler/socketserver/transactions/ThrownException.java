package org.prevayler.socketserver.transactions;

import java.io.Serializable;

/**
 * A class that encapsulates exceptions that were caught and need to be 
 * re-thrown on the client.
 * 
 * This class is for internal use only.
 * 
 * @author djo
 */
public class ThrownException implements Serializable {
    public Exception exception;
    
    public ThrownException(Exception e) {
        exception = e;
    }
}
