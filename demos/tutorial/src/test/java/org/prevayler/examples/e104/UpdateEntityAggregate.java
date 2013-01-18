package org.prevayler.examples.e104;

import org.prevayler.Transaction;

import java.util.Date;


public class UpdateEntityAggregate implements Transaction<Root> {

  private String entityIdentity;
  private String aggregateIdentity;

  public UpdateEntityAggregate() {
  }

  public UpdateEntityAggregate(String entityIdentity, String aggregateIdentity) {
    this.entityIdentity = entityIdentity;
    this.aggregateIdentity = aggregateIdentity;
  }

  public void executeOn(Root prevalentSystem, Date executionTime) {

    prevalentSystem.getEntities().get(entityIdentity).setAggregate(
        prevalentSystem.getEntities().get(aggregateIdentity)
    );

  }

  public String getEntityIdentity() {
    return entityIdentity;
  }

  public void setEntityIdentity(String entityIdentity) {
    this.entityIdentity = entityIdentity;
  }

  public String getAggregateIdentity() {
    return aggregateIdentity;
  }

  public void setAggregateIdentity(String aggregateIdentity) {
    this.aggregateIdentity = aggregateIdentity;
  }
}
