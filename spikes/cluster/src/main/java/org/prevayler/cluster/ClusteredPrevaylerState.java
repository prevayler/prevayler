package org.prevayler.cluster;

import java.io.Serializable;
import java.util.Date;

class ClusteredPrevaylerState<P extends Serializable> implements Serializable {
  
  private static final long serialVersionUID = 5823210882529956138L;
  
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
