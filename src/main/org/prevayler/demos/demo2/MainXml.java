// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.demo2;

import org.prevayler.demos.demo2.business.Bank;
import org.prevayler.implementation.PrevaylerImpl;
import org.prevayler.implementation.XmlSnapshotManager;
import org.prevayler.implementation.clock.MachineClock;
import org.prevayler.implementation.log.TransactionLogger;

public class MainXml {

	public static void main(String[] args) throws Exception {
		out("A snapshot using Skaringa's XML serialization will be taken every 20 seconds...");

		PrevaylerImpl prevayler = new PrevaylerImpl(new Bank(), new XmlSnapshotManager("demo2Xml"), new TransactionLogger("demo2Xml", new MachineClock()));

		Main.startSnapshots(prevayler);

	}

	private static void out(String message) {
		System.out.println(message);
	}		
}