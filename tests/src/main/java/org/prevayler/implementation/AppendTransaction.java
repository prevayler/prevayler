package org.prevayler.implementation;

import org.prevayler.Transaction;

import java.util.Date;

public class AppendTransaction implements Transaction<StringBuffer> {

  private static final long serialVersionUID = -3830205386199825379L;
  public String toAdd;

  @SuppressWarnings("unused")
  private AppendTransaction() {
    // Skaringa requires a default constructor, but XStream does not.
  }

  public AppendTransaction(String toAdd) {
    this.toAdd = toAdd;
  }

  public void executeOn(StringBuffer prevalentSystem, Date executionTime) {
    prevalentSystem.append(toAdd);
  }

}
