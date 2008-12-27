//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.clock;

import java.util.Date;

import org.prevayler.*;

/** A deterministic Clock that always returns the same time until it is forced to advance. This class is useful as a Clock mock in order to run automated tests involving date/time related rules. A new BrokenClock's time() starts off at new Date(0);
 */
public class BrokenClock implements Clock {

	private Date _time;
	protected long _millis;

	public BrokenClock() {
		this(new Date(0));
	}

	public BrokenClock(Date time) {
		_time = time;
		_millis = time.getTime();
	}

	public synchronized Date time() { return _time; }

	public synchronized void advanceTo(Date newTime) {
		long newMillis = newTime.getTime();
		if (newMillis == _millis) return;
		_millis = newMillis;
		_time = newTime;
	}

}
