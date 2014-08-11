package org.prevayler.examples.e101;

import org.prevayler.Transaction;

import java.io.Serializable;
import java.util.Date;

public class UpdatePersonNameTransaction implements Serializable, Transaction<Root> {

  /**
   * java.io.Serializable with a non changing serialVersionUID
   * will automatically handle backwards compatibility
   * if you add new non transient fields the the class.
   */
  private static final long serialVersionUID = 1l;

  private String identity;
  private String name;

  public UpdatePersonNameTransaction() {
  }

  public UpdatePersonNameTransaction(String identity, String name) {
    this.identity = identity;
    this.name = name;
  }

  public void executeOn(Root prevalentSystem, Date executionTime) {
    prevalentSystem.getPersons().get(identity).setName(name);
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
