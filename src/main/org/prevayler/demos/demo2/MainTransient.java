package org.prevayler.demos.demo2;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.demos.demo2.business.Bank;



public class MainTransient {
	
	public static void main(String[] ignored) throws Exception {
		out(    "This demo shows how persistence can be turned off"
			+ "\nwithout changing ONE SINGLE LINE OF CODE in the"
			+ "\nbusiness classes or GUI. This is useful for"
			+ "\nrunning automated test scripts orders of magnitude"
			+ "\nfaster than with persistence turned on."
			+ "\n(Pay no attention to the 'Robustness Reminder' this time ;)"
		);

		//Below is the single line that was changed from Main.java to disable transaction journalling. Notice the use of the AbstractPublisher:
		Prevayler prevayler = PrevaylerFactory.createTransientPrevayler(new Bank());

		Main.startGui(prevayler);
	}


	private static void out(String message) {
		System.out.println(message);
	}		

}
