//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.foundation;

public class StopWatch {

	private final long t0 = System.currentTimeMillis();

	static public StopWatch start() {
		return new StopWatch();
	}

	public long millisEllapsed() {
		return System.currentTimeMillis() - t0;
	}

	public double secondsEllapsed() {
		return millisEllapsed() / 1000.0;
	}

	private StopWatch() {
	}
}
