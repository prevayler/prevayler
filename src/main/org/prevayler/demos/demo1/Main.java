// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld, Paul Hammant
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

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
