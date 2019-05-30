package org.prevayler.examples.common;

import java.io.Serializable;

public class Member implements Serializable {

  private static final long serialVersionUID = 1L;

  private final int number;
  private String name;

  Member(int number, String name) {
    this.name = name;
    this.number = number;
  }

  public int number() {
    return number;
  }

  public void setName(String newName) {
    name = newName;
  }

  public String name() {
    return name;
  }

}
