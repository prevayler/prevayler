// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.test;

import java.io.*;
import java.util.*;

import org.prevayler.*;
import org.prevayler.implementation.*;
import org.prevayler.convenience.*;

public class ClockTest {

	static private SnapshotPrevayler prevayler;
	static private String prevalenceBase;

	static private ClockMock clockMock = new ClockMock(0);

	static public void run() throws Exception {

		newPrevalenceBase();

		crashRecover(); //There is nothing to recover at first. A new system will be created.
		verify(0);
		verify(0);

		setTime(1);
		verify(1);

		setTime(2);
		verify(2);
		verify(2);

		crashRecover();
		verify(2);

		setTime(3);
		setTime(4);
		verify(4);

		crashRecover();
		crashRecover();
		verify(4);

		setTime(10);
		verify(10);
		snapshot();
		verify(10);

		setTime(11);
		snapshot();
		crashRecover();
		verify(11);
		setTime(12);
		verify(12);
	}

	static private void crashRecover() throws Exception {
		out("CrashRecovery.");
		clockMock = new ClockMock(clockMock.currentTimeMillis());   //A new one is created because a paused clock is needed.
		prevayler = new SnapshotPrevayler(new AbstractPrevalentSystem() {}, prevalenceBase(), clockMock);
		clockMock = (ClockMock)system().clock();   //It might have been recovered from a snapshot.
	}

	static private void snapshot() throws IOException {
		out("Snapshot.");
		prevayler.takeSnapshot();
	}


	static private void setTime(long newTime) throws Exception {
		out("Setting time: " + newTime);
		clockMock.currentTimeMillis(newTime);
		prevayler.executeCommand(new NullCommand());
	}

	static private void verify(long expectedTime) {
		out("Expecting time: " + expectedTime);
		compare(system().clock().time().getTime(), expectedTime, "Time");
	}


	static private PrevalentSystem system() {
		return prevayler.system();
	}


	static private String prevalenceBase() {
		if (prevalenceBase == null) {
			prevalenceBase = "PrevalenceBase" + System.currentTimeMillis();
		}
		return prevalenceBase;
	}


	static private void newPrevalenceBase() {
		prevalenceBase = null;
	}

	static private void compare(long observed, long expected, String measurement) {
		verify(observed == expected, measurement + ": " + observed + "   Expected: " + expected);
	}

	static private void verify(boolean condition, String message) {
		if (!condition) {
			throw new RuntimeException(message);
		}
	}

	static private void out(Object obj) {
		System.out.println(obj);   //Uncomment this line to see what the test is doing.
	}
}
