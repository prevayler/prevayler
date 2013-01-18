package org.prevayler.examples.e103;

import org.prevayler.TransactionWithQuery;

import java.io.Serializable;
import java.util.Date;


public class CreateEntityTransaction implements Serializable, TransactionWithQuery<Root, Entity> {

  private static final long serialVersionUID = 1l;

  private String identity;

  public CreateEntityTransaction() {
  }

  public CreateEntityTransaction(String identity) {
    this.identity = identity;
  }

  public Entity executeAndQuery(Root prevalentSystem, Date executionTime) throws Exception {
    Entity entity = new Entity();
    entity.setIdentity(identity);
    entity.setCreated(executionTime.getTime());
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
