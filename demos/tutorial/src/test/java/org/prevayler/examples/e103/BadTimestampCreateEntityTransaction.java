package org.prevayler.examples.e103;

import org.prevayler.TransactionWithQuery;

import java.io.Serializable;
import java.util.Date;


public class BadTimestampCreateEntityTransaction implements Serializable, TransactionWithQuery<Root, Entity> {

  private static final long serialVersionUID = 1l;

  private String identity;

  public BadTimestampCreateEntityTransaction() {
  }

  public BadTimestampCreateEntityTransaction(String identity) {
    this.identity = identity;
  }

  public Entity executeAndQuery(Root prevalentSystem, Date executionTime) throws Exception {
    Entity entity = new Entity();
    entity.setIdentity(identity);
    entity.setCreated(System.currentTimeMillis()); // This line of code in bad
    // as it will set #created to time of when journal is replayed
    // rather than the initial execution time of the transaction.
    prevalentSystem.getEntities().put(entity.getIdentity(), entity);
    return entity;
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }
}
