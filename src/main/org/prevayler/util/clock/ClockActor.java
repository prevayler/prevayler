// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2002 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.util.clock;

import java.util.Date;
import org.prevayler.Prevayler;


/** You can use a ClockActor to advance the time in your ClockedSystem. It will continuously execute ClockTick transactions on the given Prevayler.
 */
public class ClockActor extends Thread {

	private final Clock _clock;
	private final Prevayler _prevayler;

	private Date _lastTime = null;


    /** Creates a ClockActor that uses a MachineClock as its "real" clock.
	 * @param prevayler prevayler.system() must be an instance of ClockedSystem.
	 */
	public ClockActor(Prevayler prevayler) {
		this(new MachineClock(), prevayler);
	}

	/** Creates a ClockActor.
	 * @param clock The Clock that will be used as the "real" time source.
	 * @param prevayler prevayler.system() must be an instance of ClockedSystem.
	 */ 
	public ClockActor(Clock clock, Prevayler prevayler) {
		_clock = clock;
		_prevayler = prevayler;
		setDaemon(true);
		start();
	}


	public void run() {
		try {
			while (true) {
				tick();
				Thread.sleep(1);
			}
		} catch (InterruptedException i) {
			//An interrupted ClockActor will just stop running.
		}
	}


	private void tick() {
		Date newTime = _clock.time();
		if (newTime == _lastTime) return;
		_lastTime = newTime;
		_prevayler.execute(new ClockTick(newTime));
	}
}
