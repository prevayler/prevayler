// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.demo2;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.demos.demo2.business.Bank;

public class MainReplica {

    public static void main(String[] args) throws Exception {
        out("This demo shows how your application can be replicated" + "\nwithout changing ONE SINGLE LINE OF CODE in the" + "\nbusiness classes or GUI. This enables query load-" + "\nbalancing and system fault-tolerance.\n\n");

        String serverURI;
        if (args.length == 1) {
            serverURI = args[0];
        } else {
            out("Usage:   java MainReplica <Server IP Address>" + "\nExample: java MainReplica 10.42.10.5" + "\n\nBefore that, though, you must run: java MainReplicaServer" + "\non this machine or any other in your network, if you haven't" + "\nalready done so.\n" + "\nTrying to find server on localhost...");

            serverURI = "localhost";
        }

        PrevaylerFactory<Bank> factory = new PrevaylerFactory<Bank>();
        factory.configurePrevalentSystem(new Bank());
        factory.configurePrevalenceDirectory("demo2Replica");
        factory.configureReplicationClient(serverURI, PrevaylerFactory.DEFAULT_REPLICATION_PORT);
        Prevayler<Bank> prevayler = factory.create();

        Main.startSnapshots(prevayler);
    }

    private static void out(String message) {
        System.out.println(message);
    }

}
