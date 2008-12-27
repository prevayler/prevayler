/*
 * NetworkSessionId.java
 *
 */
package org.prevayler.foundation.network;

import java.io.Serializable;



/**
 * Am immutable session identifier used to correlate sessions.
 * 
 *
 */
public class NetworkSessionId implements Serializable {
    private static final long serialVersionUID = -1158241173764280952L;
    private final long timeStamp;
    private final int id;
    
    protected NetworkSessionId (long timeStamp, int id) {
        this.timeStamp = timeStamp;
        this.id = id;
    }
    
    public boolean equals (Object other) {
        if (other == null) {return false;}
        if (!(other instanceof NetworkSessionId)) {return false;}
        if (((NetworkSessionId)other).timeStamp != timeStamp) {return false;}
        if (((NetworkSessionId)other).id != id) {return false;}
        return true;
    }

    public int hashCode() {
        return id;
    }
    
    public String toString() {
        return "NetworkSessionId for " + id + ", timestamped.." +timeStamp;
    }
}
