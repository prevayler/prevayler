package org.prevayler.implementation;

import org.prevayler.TransactionWithQuery;

import java.util.Date;

public class AppendTransactionWithQuery implements TransactionWithQuery<StringBuffer, String> {

  private static final long serialVersionUID = 7725358482908916942L;
  public String toAdd;

  @SuppressWarnings("unused")
  private AppendTransactionWithQuery() {
    // Skaringa requires a default constructor, but XStream does not.
  }

  public AppendTransactionWithQuery(String toAdd) {
    this.toAdd = toAdd;
  }

  public String executeAndQuery(StringBuffer prevalentSystem, Date executionTime) throws Exception {

    prevalentSystem.append(toAdd);
    return prevalentSystem.toString();
  }

}
