// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.util.clock;

import java.util.Date;


/** A system that has a clock which it used for all its date/time related functions.
 * If any business object were to access the system clock directly, with methods like new Date() and System.currentTimeMillis(), its behaviour would NOT be deterministic. You can use this as the base class for your own prevalent systems.
 */
public abstract class AbstractClockedSystem implements java.io.Serializable, ClockedSystem {

	private BrokenClock _brokenClock = new BrokenClock();


	/** Returns the clock used by this system for ALL its date/time related functions.
	 */
	public Clock clock() { return _brokenClock; }


	public void advanceClockTo(Date newTime) {
		_brokenClock.advanceTo(newTime);
	}

}
