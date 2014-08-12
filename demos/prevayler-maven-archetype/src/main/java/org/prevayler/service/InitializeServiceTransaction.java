package org.prevayler.service;


import org.prevayler.Transaction;

import java.io.Serializable;
import java.util.Date;

public class InitializeServiceTransaction implements Serializable, Transaction<Root> {

  /**
   * java.io.Serializable with a non changing serialVersionUID
   * will automatically handle backwards compatibility
   * if you add new non transient fields the the class.
   */
  private static final long serialVersionUID = 1l;

  public void executeOn(Root root, Date executionTime) {

    root.setCreated(executionTime.getTime());

  }


}