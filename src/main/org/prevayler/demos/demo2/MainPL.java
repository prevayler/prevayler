package org.prevayler.demos.demo2;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.demos.demo2.business.Bank;
import org.prevayler.foundation.serialization.PLSerializer;

public class MainPL {
    public static void main(String[] args) throws Exception {
        out("A snapshot using PL's plist serialization will be taken every 20 seconds...");

        PrevaylerFactory factory = new PrevaylerFactory();
        factory.configurePrevalenceDirectory("demo2PL");
        factory.configurePrevalentSystem(new Bank());
        factory.configureJournalSerializer(new PLSerializer());
        factory.configureSnapshotSerializer(new PLSerializer());
        Prevayler prevayler = factory.create();

        Main.startSnapshots(prevayler);
    }

    private static void out(String message) {
        System.out.println(message);
    }
}