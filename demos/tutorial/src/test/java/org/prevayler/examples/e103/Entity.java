package org.prevayler.examples.e103;

import java.io.Serializable;


public class Entity implements Serializable {

  private static final long serialVersionUID = 1l;

  private String identity;
  private long created;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Entity entity = (Entity) o;

    if (identity != null ? !identity.equals(entity.identity) : entity.identity != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return identity != null ? identity.hashCode() : 0;
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }

  public long getCreated() {
    return created;
  }

  public void setCreated(long created) {
    this.created = created;
  }
}
