//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a query that can be executed on a Prevalent System.
 *
 * @param <P> The type or any supertype of the Prevalent System you intend to perform the query upon. <br>
 * @param <R> The type of object which should be returned. <br>
 * @see Prevayler#execute(Query)
 */
public interface Query<P, R> extends Serializable {

  /**
   * @param prevalentSystem The Prevalent System to be queried.
   * @param executionTime   The "current" time.
   * @return The result of this Query.
   * @throws Exception Any Exception encountered by this Query.
   */
  public R query(P prevalentSystem, Date executionTime) throws Exception;

}
