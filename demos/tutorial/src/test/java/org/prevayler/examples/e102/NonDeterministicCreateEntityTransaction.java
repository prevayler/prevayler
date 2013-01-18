package org.prevayler.examples.e102;

import org.prevayler.TransactionWithQuery;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;


public class NonDeterministicCreateEntityTransaction implements Serializable, TransactionWithQuery<Root, Entity> {

  private static final long serialVersionUID = 1l;

  public NonDeterministicCreateEntityTransaction() {
  }


  public Entity executeAndQuery(Root prevalentSystem, Date executionTime) throws Exception {

    Entity entity = new Entity();
    entity.setIdentity(UUID.randomUUID().toString()); // This line of code is non deterministic
    // since a new identity will be assigned to the object every time the journal is replayed at startup.
    // This will severely mess with your system.

    prevalentSystem.getEntities().put(entity.getIdentity(), entity);
    return entity;
  }

}
