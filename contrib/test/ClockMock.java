// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.test;

import org.prevayler.implementation.SystemClock;

/** A controllable Clock that can be used for running functional tests for business rules involving time.
*/
public class ClockMock extends SystemClock {

	private long currentTimeMillis;

	/** Creates an ClockMock with the internal "current" time set to currentTimeMillis.
	*/
	public ClockMock(long currentTimeMillis) {
		this.currentTimeMillis = currentTimeMillis;
	}

	/** Sets the internal "current" time of the clock.
	* @throws IllegalArgumentException if newMillis is smaller or equal to the current time. An Clock's time can only progress forwards.
	*/
	public synchronized void currentTimeMillis(long newMillis) {
		if (newMillis <= currentTimeMillis) throw new IllegalArgumentException("Clock's time can only be set forwards.");
		currentTimeMillis = newMillis;
	}

	public long currentTimeMillis() {
		return currentTimeMillis;
	}
}
