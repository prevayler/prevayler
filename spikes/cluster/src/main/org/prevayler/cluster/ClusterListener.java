package org.prevayler.cluster;

public interface ClusterListener {
    Object getState();

    void setState(Object state);
    
    Object receive(Object message);
}
