package org.prevayler.examples.e105;

import org.prevayler.Transaction;

import java.io.Serializable;
import java.util.Date;

public class UpdateEntityTextTransaction implements Serializable, Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private String identity;
  private String text;

  public UpdateEntityTextTransaction() {
  }

  public UpdateEntityTextTransaction(String identity, String text) {
    this.identity = identity;
    this.text = text;
  }

  public void executeOn(Root prevalentSystem, Date executionTime) {
    prevalentSystem.getEntities().get(identity).setText(text);
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
