package org.prevayler.demos.demo2;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.demos.demo2.business.Bank;
import org.prevayler.foundation.serialization.SkaringaSerializer;

public class MainSkaringa {

	public static void main(String[] args) throws Exception {
		out("A snapshot using Skaringa's XML serialization will be taken every 20 seconds...");

		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalenceDirectory("demo2Skaringa");
		factory.configurePrevalentSystem(new Bank());
		factory.configureSnapshotSerializer(new SkaringaSerializer());
		Prevayler prevayler = factory.create();

		Main.startSnapshots(prevayler);
	}

	private static void out(String message) {
		System.out.println(message);
	}		
}