package org.prevayler.cluster;

import java.util.Date;
import java.io.Serializable;

class ClusteredPrevaylerState implements Serializable {
    private Object system;
    private Date time;

    public ClusteredPrevaylerState(Object system, Date time) {
        this.system = system;
        this.time = time;
    }

    public Object getSystem() {
        return system;
    }

    public Date getTime() {
        return time;
    }
}
