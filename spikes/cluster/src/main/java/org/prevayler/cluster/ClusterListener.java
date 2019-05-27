package org.prevayler.cluster;

import java.io.Serializable;

public interface ClusterListener<P extends Serializable> {
  Object getState();

  void setState(P prevalentSystem);

  Object receive(ClusteredTransaction<P> transaction);
}
