package org.prevayler.demos.demo2;

import org.prevayler.implementation.*;
import org.prevayler.demos.demo2.gui.BankFrame;

public class NullMain {
	
	public static void main(String[] args) {
		out("Using NullPrevayler. System state will NOT be persisted.");
		new BankFrame(new NullPrevayler(new Bank()));
	}
	
	private static void out(String message) {
		System.out.println(message);
	}		

}
