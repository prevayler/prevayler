package org.prevayler.demos.demo1;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import java.io.*;


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
