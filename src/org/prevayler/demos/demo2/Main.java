package org.prevayler.demos.demo2;

import org.prevayler.*;
import org.prevayler.implementation.*;
import org.prevayler.demos.demo2.gui.BankFrame;
import java.io.*;

public class Main {
	
	public static void main(String[] args) {
		try{

			run();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void run() throws IOException, ClassNotFoundException, InterruptedException {
		SnapshotPrevayler prevayler = new SnapshotPrevayler(new Bank());
		new BankFrame(prevayler);
		
		out("A system snapshot will be taken every 24h...");
		while (true) {
			Thread.sleep(1000 * 60 * 60 * 24);
			prevayler.takeSnapshot();
			out("Snapshot taken at " + new java.util.Date() + "...");
		}
	}

	private static void out(String message) {
		System.out.println(message);
	}		

}
