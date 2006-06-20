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

    protected NetworkSessionId(long timeStamp, int id) {
        this.timeStamp = timeStamp;
        this.id = id;
    }

    @Override public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof NetworkSessionId)) {
            return false;
        }
        if (((NetworkSessionId) other).timeStamp != timeStamp) {
            return false;
        }
        if (((NetworkSessionId) other).id != id) {
            return false;
        }
        return true;
    }

    @Override public int hashCode() {
        return id;
    }

    @Override public String toString() {
        return "NetworkSessionId for " + id + ", timestamped.." + timeStamp;
    }
}
