package org.prevayler.examples.e101;

import org.prevayler.TransactionWithQuery;

import java.io.Serializable;
import java.util.Date;

public class DeletePersonTransaction implements TransactionWithQuery<Root, Person>, Serializable {

  /**
   * java.io.Serializable with a non changing serialVersionUID
   * will automatically handle backwards compatibility
   * if you add new non transient fields the the class.
   */
  private static final long serialVersionUID = 1l;

  private String identity;

  public DeletePersonTransaction() {
  }

  public DeletePersonTransaction(String identity) {
    this.identity = identity;
  }

  public Person executeAndQuery(Root prevalentSystem, Date executionTime) throws Exception {
    return prevalentSystem.getPersons().remove(identity);
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }
}
