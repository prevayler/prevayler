// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler;

import java.io.Serializable;


/**
 * A system that implements this interface can be made transparently persistent with Prevayler. All business objects in this system must be deterministic. i.e.: Any two PrevalentSystems of the same class must produce the same results given the same commands.
 * @see org.prevayler.Prevayler
 */
public interface PrevalentSystem extends Serializable {

  /**
   * Sets the clock to be used by this system for ALL its date/time related functions. This method is called only once by Prevayler during the first system initialization.
   */
  public void clock(AlarmClock clock);

  /**
   * Returns the clock used by this system for ALL its date/time related functions.
   */
  public AlarmClock clock();

}
