// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.util;

import java.util.Date;


/** A Clock that always returns the same time until it is forced to advance. A new BrokenClock's time() starts off at new Date(Long.MIN_VALUE);
 */
class BrokenClock implements Clock, java.io.Serializable {

	private long _millis = Long.MIN_VALUE;
	private Date _time = new Date(_millis);


	public Date time() {
		return _time;
	}


	void advanceTo(long newMillis) {
		if (newMillis < _millis) throw new RuntimeException("Attempt to set Clock to the past. From: " + _time + " back to: " + new Date(newMillis));
		if (newMillis == _millis) return;
		_millis = newMillis;
		_time = new Date(_millis);
	}

}
