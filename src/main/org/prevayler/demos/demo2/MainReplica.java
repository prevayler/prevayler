package org.prevayler.demos.demo2;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.demos.demo2.business.Bank;


public class MainReplica {
// TODO Fix exception that is thrown when replicating with no journal created. 
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
		factory.configurePrevalenceDirectory("demo2Replica");
		factory.configureReplicationClient(args[0], PrevaylerFactory.DEFAULT_REPLICATION_PORT);
		Prevayler prevayler = factory.create();

		Main.startSnapshots(prevayler);
	}


	private static void out(String message) {
		System.out.println(message);
	}		

}
