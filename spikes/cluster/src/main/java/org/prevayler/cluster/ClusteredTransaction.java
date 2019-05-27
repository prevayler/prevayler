package org.prevayler.cluster;

import org.prevayler.Transaction;
import org.prevayler.cluster.ClusteredPrevayler;

import java.io.Serializable;
import java.util.Date;

class ClusteredTransaction<P extends Serializable> implements Serializable {
  
  private static final long serialVersionUID = -3346683075172200979L;
  
  private Transaction<? super P> transaction;
  @SuppressWarnings("unused")
  private Date executionTime;

  public ClusteredTransaction(Transaction<? super P> transaction, Date executionTime) {
    this.transaction = transaction;
    this.executionTime = executionTime;
  }

  public Object executeOn(ClusteredPrevayler<P> prevayler) {
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
