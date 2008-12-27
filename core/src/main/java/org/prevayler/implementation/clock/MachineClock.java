//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.clock;

import java.util.Date;

/** A Clock that uses the local machine clock (System.currentTimeMillis()) as its time source.
 */
public class MachineClock extends BrokenClock {

	/** @return The local machine time.
	*/
	public synchronized Date time() {
		update();
		return super.time();
	}


	private synchronized void update() {
		long newTime = System.currentTimeMillis();
		if (newTime != _millis) advanceTo(new Date(newTime));
	}

}
