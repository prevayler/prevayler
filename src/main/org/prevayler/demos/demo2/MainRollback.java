// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.demo2;

import org.prevayler.demos.demo2.business.RollbackBank;
import org.prevayler.demos.demo2.gui.BankFrame;
import org.prevayler.implementation.RollbackPrevayler;
import org.prevayler.util.clock.ClockActor;

public class MainRollback {
	
	public static void main(String[] ignored) throws Exception {
        out("There's a 50% chance of some transactions failing AFTER the system has been modified. " +
                "This will result in the state of the system being roll backed to the state before the transaction started.");
		RollbackPrevayler prevayler = new RollbackPrevayler(new RollbackBank(), "demo2Acid");
		new ClockActor(prevayler);

		new BankFrame(prevayler);

		out("One snapshot per day is more than enough for most applications because the transactionLog recovery rate is in the order of 6000 transactions per second. For demoing purposes, though, a snapshot will be taken every 20 seconds...");
		while (true) {
			Thread.sleep(1000 * 20);
			prevayler.takeSnapshot();
			out("Snapshot taken at " + new java.util.Date() + "...");
		}
	}
	
	private static void out(String message) {
		System.out.println(message);
	}		

}
