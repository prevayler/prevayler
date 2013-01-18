package org.prevayler.examples.e104;

import org.prevayler.Transaction;

import java.util.Date;


public class PrevalentInitiationProblemUpdateEntityAggregate implements Transaction<Root> {

  private String entityIdentity;
  private Entity aggregate;

  public PrevalentInitiationProblemUpdateEntityAggregate() {
  }

  public PrevalentInitiationProblemUpdateEntityAggregate(String entityIdentity, Entity aggregate) {
    this.entityIdentity = entityIdentity;
    this.aggregate = aggregate;
  }

  public void executeOn(Root prevalentSystem, Date executionTime) {

    prevalentSystem.getEntities().get(entityIdentity).setAggregate(aggregate); // This line of code is bad
    // since it will set #aggregate to be a deep clone of the business object
    // rather than referencing the instance available in the prevalent system.
    // Correct would be to look up the instance using its identity the same way
    // as the entity is looked up. It could simply be fixed by accessing aggregate.getIdentity(),
    // but that would be rather silly (and bad in many other ways too)
    // to serialize the whole aggregate in the transaction instead of just the identity.
  }

  public String getEntityIdentity() {
    return entityIdentity;
  }

  public void setEntityIdentity(String entityIdentity) {
    this.entityIdentity = entityIdentity;
  }

  public Entity getAggregate() {
    return aggregate;
  }

  public void setAggregate(Entity aggregate) {
    this.aggregate = aggregate;
  }
}
