// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.demo2;

import org.prevayler.demos.demo2.business.Bank;
import org.prevayler.implementation.*;
import org.prevayler.implementation.replica.RemotePublisher;


public class MainReplica {

	public static void main(String[] args) throws Exception {
		out(    "This demo shows how your application can be replicated"
			+ "\nwithout changing ONE SINGLE LINE OF CODE in the"
			+ "\nbusiness classes or GUI. This enables query load-"
			+ "\nbalancing and system fault-tolerance.\n\n"
		);

		if (args.length == 0) {
			out(  "Usage:   java MainReplica <Server IP Address>"
				+ "\nExample: java MainReplica 10.42.10.5"
				+ "\nExample: java MainReplica localhost"
				+ "\n\nBefore that, though, you must run: java MainReplicaServer"
				+ "\non this machine or any other in your network, if you haven't"
				+ "\nalready done so.\n\n"
			);
			return;
		}

		//Below are the two lines that were changed from Main.java (before calling the application code) to enable replication.
		TransactionPublisher publisher = new RemotePublisher(args[0]);
		SnapshotPrevayler prevayler = new SnapshotPrevayler(new Bank(), new SnapshotManager("demo2AcidReplica"), publisher);
	
		Main.startGui(prevayler);
	}


	private static void out(String message) {
		System.out.println(message);
	}		

}
