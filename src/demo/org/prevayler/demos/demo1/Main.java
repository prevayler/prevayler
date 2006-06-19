// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.demo1;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws Exception {
        printMessage();
        Prevayler prevayler = PrevaylerFactory.createPrevayler(new NumberKeeper(), "demo1");
        new PrimeCalculator(prevayler).start();
    }

    static private void printMessage() throws Exception {
        System.out.println("\nRobustness Reminder: You can kill this process at any time.\nWhen you restart the system, you will see that nothing was lost.\nPress Enter to continue.\n");
        (new BufferedReader(new InputStreamReader(System.in))).readLine();
    }

}
