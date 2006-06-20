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
import org.prevayler.demos.demo2.gui.BankFrame;

public class Main {

    public static void main(String[] ignored) throws Exception {
        out("\nOne snapshot per day is more than enough for most applications" + "\n  because the journal recovery rate is in the order of" + "\n  6000 transactions per second. For demoing purposes, though, a" + "\n  snapshot will be taken every 20 seconds...");

        Prevayler<Bank> prevayler = PrevaylerFactory.createPrevayler(new Bank(), "demo2");
        startSnapshots(prevayler);
    }

    static void startSnapshots(Prevayler<Bank> prevayler) throws Exception {
        startGui(prevayler);

        while (true) {
            Thread.sleep(1000 * 20);
            prevayler.takeSnapshot();
        }
    }

    static void startGui(Prevayler<Bank> prevayler) {
        new BankFrame(prevayler);
    }

    private static void out(String message) {
        System.out.println(message);
    }

}
