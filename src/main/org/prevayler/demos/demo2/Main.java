// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.demo2;

import org.prevayler.Prevayler;
import org.prevayler.demos.demo2.business.Bank;
import org.prevayler.demos.demo2.gui.BankFrame;
import org.prevayler.implementation.SnapshotPrevayler;
import org.prevayler.util.PrevaylerFactory;
import org.prevayler.util.QueryPrevayler;

public class Main {
	
	public static void main(String[] ignored) throws Exception {
		out("\nOne snapshot per day is more than enough for most applications"
			+ "\n  because the transactionLog recovery rate is in the order of"
			+ "\n  6000 transactions per second. For demoing purposes, though, a"
			+ "\n  snapshot will be taken every 20 seconds...");

		SnapshotPrevayler prevayler = PrevaylerFactory.createSnapshotPrevayler(new Bank(), "demo2Acid");
		startSnapshots(prevayler);
	}

	static void startSnapshots(SnapshotPrevayler prevayler)	throws Exception {
		startGui(prevayler);

		while (true) {
			Thread.sleep(1000 * 20);
			prevayler.takeSnapshot();
			out("Snapshot taken at " + new java.util.Date() + "...");
		}
	}

	static void startGui(Prevayler prevayler) {
		new BankFrame(new QueryPrevayler(prevayler));
	}
	
	private static void out(String message) {
		System.out.println(message);
	}		

}
