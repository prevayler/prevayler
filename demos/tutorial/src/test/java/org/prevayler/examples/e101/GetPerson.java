package org.prevayler.examples.e101;

import org.prevayler.Query;

import java.util.Date;

public class GetPerson implements Query<Root, Person> {

  private String identity;

  public GetPerson(String identity) {
    this.identity = identity;
  }

  public Person query(Root prevalentSystem, Date executionTime) throws Exception {
    return prevalentSystem.getPersons().get(identity);
  }
}
