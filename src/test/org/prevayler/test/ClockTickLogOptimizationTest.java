// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.test;

import java.io.IOException;
import java.sql.Date;

import junit.framework.TestCase;

import org.prevayler.implementation.SnapshotPrevayler;
import org.prevayler.util.clock.ClockTick;

public class ClockTickLogOptimizationTest extends TestCase {

	private SnapshotPrevayler prevayler;
	private String prevalenceBase;

	public void testTicks() throws Exception {

		newPrevalenceBase();

		crashRecover(); //There is nothing to recover at first. A new system will be created.
		crashRecover();
		
		setSystemTime(45);
		clockTick(1);
		clockTick(2);
		snapshot();
		verifyClock(2);
		setSystemTimeToClock();
		crashRecover();
		verifyClock(2);
		verifySystem(2);

		clockTick(3);
		clockTick(4);
		clockTick(5);
		clockTick(6);

		crashRecover();
		verifyClock(2);
		verifySystem(2);

		clockTick(3);
		setSystemTimeToClock();
		clockTick(4);

		crashRecover();
		verifyClock(3);
		verifySystem(3);

		clockTick(4);
		clockTick(5);
		clockTick(6);
		clockTick(7);

		setSystemTimeToClock();
		crashRecover();
		verifyClock(7);
		verifySystem(7);

		clockTick(8);
		clockTick(9);
		setSystemTime(111);
		clockTick(10);
		clockTick(11);

		setSystemTime(2);
		crashRecover();
		verifyClock(11);
		verifySystem(2);

		clockTick(12);
		clockTick(13);
		clockTick(14);
		clockTick(15);

		snapshot();
		crashRecover();
		verifyClock(15);
		verifySystem(2);

		clockTick(16);
		clockTick(17);
		clockTick(18);
		clockTick(19);

		setSystemTime(287);
		snapshot();
		crashRecover();
		verifyClock(19);
		verifySystem(287);

	}

	protected void clockTick(long expectedTime) throws Exception {
		out("Expecting clock time: " + expectedTime);
		prevayler.execute(new ClockTick(new Date(expectedTime)));		
		compare(system().clock().time().getTime(), expectedTime, "Clock time");
	}
	
	private void setSystemTime(long expectedTime) throws Exception {
		out("Expecting system time: " + expectedTime);
		prevayler.execute(new Tick(expectedTime));
		verifySystem(expectedTime);
	}

	private void setSystemTimeToClock() throws Exception {
		long expectedTime = system().clock().time().getTime();
		out("Expecting system time: " + expectedTime);
		prevayler.execute(new Tick(expectedTime));
		verifySystem(expectedTime);
	}

	private void verifyClock(long expectedTime) {
		out("Expecting clock time:" + expectedTime);
		compare(system().clock().time().getTime(), expectedTime, "Clock time");
	}

	private void verifySystem(long expectedTime) {
		out("Expecting system time: " + expectedTime);
		compare(system().time().getTime(), expectedTime, "System time");
	}

	private TickingSystem system() {
		return (TickingSystem)prevayler.prevalentSystem();
	}

	private String prevalenceBase() {
		return prevalenceBase;
	}

	private void newPrevalenceBase() throws Exception {
		prevalenceBase = "PrevalenceBase" + System.currentTimeMillis();
	}

	private void crashRecover() throws Exception {
		out("CrashRecovery.");
		prevayler = new SnapshotPrevayler(new TickingSystem(), prevalenceBase());
	}

	private void snapshot() throws IOException {
		out("Snapshot.");
		prevayler.takeSnapshot();
	}

	private void compare(long observed, long expected, String measurement) {
		verify(observed == expected, measurement + ": " + observed + "   Expected: " + expected);
	}

	private static void verify(boolean condition, String message) {
		if (!condition) {
			throw new RuntimeException(message);
		}
	}

	private static void out(Object obj) {
		System.out.println(obj);   //Uncomment this line to see what the test is doing.
	}
	protected void tearDown() throws Exception {
		RollbackTest.delete(prevalenceBase);
	}

}
