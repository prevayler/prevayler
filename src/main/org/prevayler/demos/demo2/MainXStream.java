package org.prevayler.demos.demo2;

import com.thoughtworks.xstream.XStream;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.demos.demo2.business.Account;
import org.prevayler.demos.demo2.business.AccountEntry;
import org.prevayler.demos.demo2.business.Bank;
import org.prevayler.foundation.serialization.XStreamSerializer;

public class MainXStream {

	public static void main(String[] args) throws Exception {
		out("A snapshot using XStream's XML serialization will be taken every 20 seconds...");

		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalenceDirectory("demo2XStream");
		
		factory.configureSnapshotSerializer(new XStreamSerializer() {
			protected XStream createXStream() {
				XStream xstream = new XStream();
				xstream.alias("bank", Bank.class);  //This mapping is optional. It just makes the XML in the snapshot file look prettier.
				xstream.alias("account", Account.class);
				xstream.alias("accountEntry", AccountEntry.class);
				return xstream;
			}
		});
		factory.configurePrevalentSystem(new Bank());
		Prevayler prevayler = factory.create();

		Main.startSnapshots(prevayler);
	}

	private static void out(String message) {
		System.out.println(message);
	}		
}