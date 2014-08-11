package org.prevayler.examples.e101;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Root implements Serializable {

  /**
   * java.io.Serializable with a non changing serialVersionUID
   * will automatically handle backwards compatibility
   * if you add new non transient fields the the class.
   */
  private static final long serialVersionUID = 1l;

  private Map<String, Person> persons = new HashMap<String, Person>();


  public Map<String, Person> getPersons() {
    return persons;
  }

  public void setPersons(Map<String, Person> persons) {
    this.persons = persons;
  }
}
