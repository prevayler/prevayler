package org.prevayler.examples.e101;

import org.prevayler.Query;

import java.util.Date;

public class GetPerson implements Query<Root, Person> {

  private static final long serialVersionUID = 3799438221680331803L;
  private String identity;

  public GetPerson(String identity) {
    this.identity = identity;
  }

  public Person query(Root prevalentSystem, Date executionTime) throws Exception {
    return prevalentSystem.getPersons().get(identity);
  }
}
