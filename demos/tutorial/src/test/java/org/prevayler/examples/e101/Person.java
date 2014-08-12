package org.prevayler.examples.e101;

import java.io.Serializable;

public class Person implements Serializable {

  /**
   * java.io.Serializable with a non changing serialVersionUID
   * will automatically handle backwards compatibility
   * if you add new non transient fields the the class.
   */
  private static final long serialVersionUID = 1l;

  private String identity;
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Person person = (Person) o;

    if (identity != null ? !identity.equals(person.identity) : person.identity != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return identity != null ? identity.hashCode() : 0;
  }
}
