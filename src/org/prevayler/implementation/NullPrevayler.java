// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2002 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import org.prevayler.*;
import java.io.Serializable;

/**
 * A dummy Prevayler implementation that DOES NOT persist the command log files nor the system state.
 * Used for running demos or test scripts against prevalent systems orders of magnitudes faster than with persistence turned on.
 */
public class NullPrevayler implements Prevayler {

	private final PrevalentSystem system;

	/** Creates a NullPrevayler for the given PrevalentSystem. A newly created SystemClock will be used as the AlarmClock for the system.
	* @param newSystem The newly started, "empty" PrevalentSystem.
	*/
	public NullPrevayler(PrevalentSystem newSystem) {
		this(newSystem, defaultClock());
	}

	/** Creates a NullPrevayler for the given PrevalentSystem using the given AlarmClock.
	* @param newSystem The newly started, "empty" PrevalentSystem.
	* @param alarmClock The clock to be used by the system.
	*/
	public NullPrevayler(PrevalentSystem newSystem, AlarmClock alarmClock) {
		newSystem.clock(alarmClock);
		system = newSystem;
	}


	/** Returns the underlying PrevalentSystem.
	*/
	public PrevalentSystem system() {
		return system;
	}


	/** Executes the given Command on the underlying PrevalentSystem WITHOUT saving it to a log file.
	* @see #system()
	* @return The serializable object that was returned by the execution of command.
	* @throws Exception if command.execute(PrevalentSystem) throws an exception.
	*/
	public Serializable executeCommand(Command command) throws Exception {
		return command.execute(system);
	}


	private static AlarmClock defaultClock() {
		SystemClock clock = new SystemClock();
		clock.resume();
		return clock;
	}

}
