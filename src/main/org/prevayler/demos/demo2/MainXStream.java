package org.prevayler.demos.demo2;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.demos.demo2.business.Bank;
import org.prevayler.implementation.snapshot.XStreamSnapshotManager;

public class MainXStream {

	public static void main(String[] args) throws Exception {
		out("A snapshot using XStream's XML serialization will be taken every 20 seconds...");

		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalenceBase("demo2XStream");
		factory.configureSnapshotManager(new XStreamSnapshotManager(new Bank(), "demo2XStream"));
		Prevayler prevayler = factory.create();

		Main.startSnapshots(prevayler);

	}

	private static void out(String message) {
		System.out.println(message);
	}		
}