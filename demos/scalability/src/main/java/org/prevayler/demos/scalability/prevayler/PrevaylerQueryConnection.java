package org.prevayler.demos.scalability.prevayler;

import org.prevayler.demos.scalability.QueryConnection;
import org.prevayler.demos.scalability.Record;

import java.util.List;

class PrevaylerQueryConnection implements QueryConnection {

  private final QuerySystem querySystem;


  PrevaylerQueryConnection(QuerySystem querySystem) {
    this.querySystem = querySystem;
  }


  public List<Record> queryByName(String name) {
    return querySystem.queryByName(name);
  }
}
