// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

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
