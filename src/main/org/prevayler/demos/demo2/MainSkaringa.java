package org.prevayler.demos.demo2;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.demos.demo2.business.Bank;
import org.prevayler.implementation.snapshot.SkaringaSnapshotManager;

public class MainSkaringa {

	public static void main(String[] args) throws Exception {
		out("A snapshot using Skaringa's XML serialization will be taken every 20 seconds...");

		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalenceBase("demo2Skaringa");
		factory.configureSnapshotManager(new SkaringaSnapshotManager(new Bank(), "demo2Skaringa"));
		Prevayler prevayler = factory.create();

		Main.startSnapshots(prevayler);

	}

	private static void out(String message) {
		System.out.println(message);
	}		
}