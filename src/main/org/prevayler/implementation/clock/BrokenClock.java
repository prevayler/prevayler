// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation.clock;

import java.util.Date;

import org.prevayler.*;


/** A deterministic Clock that always returns the same time until it is forced to advance. This class is useful as a Clock mock in order to run automated tests involving date/time related rules. A new BrokenClock's time() starts off at new Date(0);
 */
public class BrokenClock implements Clock, java.io.Serializable {

	private Date _time;
	protected long _millis;

	public BrokenClock() {
			this(new Date(0));
	}

	public BrokenClock(Date time) {
		_time = time;
		_millis = time.getTime();
	}

	public Date time() { return _time; }

	public synchronized void advanceTo(Date newTime) {
		long newMillis = newTime.getTime();
		if (newMillis == _millis) return;
		_millis = newMillis;
		_time = newTime;
	}

}
