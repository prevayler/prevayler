package org.prevayler.examples.e101;

import org.prevayler.TransactionWithQuery;

import java.io.Serializable;
import java.util.Date;

public class CreatePersonTransaction implements TransactionWithQuery<Root, Person>, Serializable {

  private static final long serialVersionUID = 1l;

  private String identity;

  public CreatePersonTransaction() {
  }

  public CreatePersonTransaction(String identity) {
    this.identity = identity;
  }

  public Person executeAndQuery(Root prevalentSystem, Date executionTime) throws Exception {
    Person entity = new Person();
    entity.setIdentity(identity);
    prevalentSystem.getPersons().put(entity.getIdentity(), entity);
    return entity;
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }
}
