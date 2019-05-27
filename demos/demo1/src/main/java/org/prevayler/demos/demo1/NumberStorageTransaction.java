package org.prevayler.demos.demo1;

import org.prevayler.Transaction;

import java.util.Date;


/**
 * To change the state of the business objects, the client code must use a Transaction like this one.
 */
class NumberStorageTransaction implements Transaction<NumberKeeper> {

  private static final long serialVersionUID = -2023934810496653301L;
  private int _numberToKeep;

  @SuppressWarnings("unused")
  private NumberStorageTransaction() {
  } //Necessary for Skaringa XML serialization

  NumberStorageTransaction(int numberToKeep) {
    _numberToKeep = numberToKeep;
  }

  public void executeOn(NumberKeeper prevalentSystem, Date ignored) {
    prevalentSystem.keep(_numberToKeep);
  }
}
