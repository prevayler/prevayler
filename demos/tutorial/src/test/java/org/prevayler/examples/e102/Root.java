package org.prevayler.examples.e102;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Root implements Serializable {

  private static final long serialVersionUID = 1l;

  private Map<String, Entity> entities = new HashMap<String, Entity>();


  public Map<String, Entity> getEntities() {
    return entities;
  }

  public void setEntities(Map<String, Entity> entities) {
    this.entities = entities;
  }
}
