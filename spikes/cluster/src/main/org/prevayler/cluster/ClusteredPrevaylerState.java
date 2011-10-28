package org.prevayler.cluster;

import java.util.Date;
import java.io.Serializable;

class ClusteredPrevaylerState<P extends Serializable> implements Serializable {
    private P system;
    private Date time;

    public ClusteredPrevaylerState(P system, Date time) {
        this.system = system;
        this.time = time;
    }

    public P getSystem() {
        return system;
    }

    public Date getTime() {
        return time;
    }
}
