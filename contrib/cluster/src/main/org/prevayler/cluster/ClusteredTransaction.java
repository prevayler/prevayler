package org.prevayler.cluster;

import java.util.Date;
import java.io.Serializable;

import org.prevayler.Transaction;
import org.prevayler.cluster.ClusteredPrevayler;

class ClusteredTransaction implements Serializable {
    private Transaction transaction;
    private Date executionTime;

    public ClusteredTransaction(Transaction transaction, Date executionTime) {
        this.transaction = transaction;
        this.executionTime = executionTime;
    }

    public Object executeOn(ClusteredPrevayler prevayler) {
        // TODO what about executionTime!
        System.out.println("Executing " + transaction);
        try {
            prevayler.executeBroadcastedTransaction(transaction);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
}
