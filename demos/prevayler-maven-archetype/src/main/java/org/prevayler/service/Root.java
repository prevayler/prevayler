package org.prevayler.service;

import java.io.Serializable;

public class Root implements Serializable {

  /**
   * java.io.Serializable with a non changing serialVersionUID
   * will automatically handle backwards compatibility
   * if you add new non transient fields the the class.
   */
  private static final long serialVersionUID = 1l;

  private Long created = null;

  public Long getCreated() {
    return created;
  }

  public void setCreated(Long created) {
    this.created = created;
  }
}