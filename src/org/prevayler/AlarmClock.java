// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler;

import java.io.Serializable;
import java.util.Date;


/** The clock used by every business object in a PrevalentSystem for ALL its date/time related functions.
 * If any business object were to access the system clock directly, with methods like new Date() and System.currentTimeMillis(), its behaviour would NOT be deterministic.
 * A future version of AlarmClock will be able to wake up other objects in a way similar to javax.swing.Timer and java.util.Timer.
 */
public interface AlarmClock extends Serializable {

  /** Returns the "current" time. The same value will be returned for every call, throughout the execution of a command, ensuring that each command is executed in a single moment in time. Without this, the commands could not be deterministically re-executed during crash-recovery.
   * @return A Date greater or equal to the one returned by the last call to this method. If the time is the same as the last call, the SAME Date object is guaranteed to be returned and not a new, equal one.
   */
  public Date time();
}
