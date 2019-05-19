package org.prevayler.demos.scalability;

import java.util.List;

public interface QueryConnection {

  /**
   * Returns the List of all Record with the given name.
   */
  public List<Record> queryByName(String name);

}
