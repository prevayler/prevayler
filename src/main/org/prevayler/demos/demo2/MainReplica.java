// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.demo2;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.demos.demo2.business.Bank;


public class MainReplica {

	public static void main(String[] args) throws Exception {
		out(    "This demo shows how your application can be replicated"
			+ "\nwithout changing ONE SINGLE LINE OF CODE in the"
			+ "\nbusiness classes or GUI. This enables query load-"
			+ "\nbalancing and system fault-tolerance.\n\n"
		);

		if (args.length != 1) {
			out(  "Usage:   java MainReplica <Server IP Address>"
				+ "\nExample: java MainReplica 10.42.10.5"
				+ "\nExample: java MainReplica localhost"
				+ "\n\nBefore that, though, you must run: java MainReplicaServer"
				+ "\non this machine or any other in your network, if you haven't"
				+ "\nalready done so.\n\n"
			);
			return;
		}

		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(new Bank());
		factory.configurePrevalenceBase("demo2AcidReplica");
		factory.configureReplicationClient(args[0]);
		Prevayler prevayler = factory.create();

		Main.startGui(prevayler);
	}


	private static void out(String message) {
		System.out.println(message);
	}		

}
