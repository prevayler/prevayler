// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.util;

import java.util.Date;


/** The clock used by every business object in a PrevalentSystem for ALL its date/time related functions.
 * If any business object were to access the system clock directly, with methods like new Date() and System.currentTimeMillis(), its behaviour would NOT be deterministic.
 */
public interface Clock extends java.io.Serializable {

  /** Tells the time. The same value will be returned for every call, throughout the execution of a transaction, ensuring that each transaction is executed in a single moment in time. Without this, the transactions could not be deterministically re-executed during crash-recovery.
   * @return A Date greater or equal to the one returned by the last call to this method. As of Prevayler 1.03.005 it is NO LONGER guaranteed that if the time is the same as the last call, the SAME Date object is returned rather than a new, equal one.
   */
  public Date time();
}
