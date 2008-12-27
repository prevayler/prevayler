package org.prevayler.demos.demo2;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.demos.demo2.business.Bank;


public class MainReplicaServer {

	public static void main(String[] ignored) throws Exception {
		out(    "This demo shows how your application can be replicated"
			+ "\nwithout changing ONE SINGLE LINE OF CODE in the"
			+ "\nbusiness classes or GUI. This enables query load-"
			+ "\nbalancing and system fault-tolerance."
			+ "\n\nThe server is up. Now you can start the replica"
			+ "\non any machine in your network:"
			+ "\n  java org.prevayler.demos.demo2.MainReplica <This machine's IP Address>\n\n"
		);

		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(new Bank());
		factory.configurePrevalenceDirectory("demo2");
		factory.configureReplicationServer(PrevaylerFactory.DEFAULT_REPLICATION_PORT);
		Prevayler prevayler = factory.create();

		Main.startGui(prevayler);
	}


	private static void out(String message) {
		System.out.println(message);
	}		

}
