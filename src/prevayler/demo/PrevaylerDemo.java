package prevayler.demo;

import prevayler.*;
import prevayler.demo.gui.BankFrame;
import java.io.*;

public class PrevaylerDemo {
	
	public static void main(String[] args) {
		try{

			run();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void run() throws IOException, ClassNotFoundException, InterruptedException {
		String prevalenceBase = System.getProperty("user.dir") + "/prevalenceBase";
		out("The following directory shall be used for the snapshot files and log files: " + prevalenceBase);

        Prevayler prevayler = new Prevayler(new Bank(), prevalenceBase);
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
