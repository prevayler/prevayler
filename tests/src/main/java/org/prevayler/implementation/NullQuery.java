package org.prevayler.implementation;

import org.prevayler.Query;

import java.util.Date;

public class NullQuery<P> implements Query<P, Object> {

  private static final long serialVersionUID = -6015707053344271489L;

  public Object query(Object prevalentSystem, Date executionTime) throws Exception {
    return null;
  }

}
