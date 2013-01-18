package org.prevayler.examples.e104;


import org.prevayler.Query;

import java.util.Date;


public class GetEntity implements Query<Root, Entity> {

  private String identity;

  public GetEntity(String identity) {
    this.identity = identity;
  }

  public Entity query(Root prevalentSystem, Date executionTime) throws Exception {
    return prevalentSystem.getEntities().get(identity);
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }
}
