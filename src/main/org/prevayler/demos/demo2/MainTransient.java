// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

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

		//Below is the single line that was changed from Main.java to disable transaction logging. Notice the use of the AbstractPublisher:
		Prevayler prevayler = PrevaylerFactory.createTransientPrevayler(new Bank());

		Main.startGui(prevayler);
	}


	private static void out(String message) {
		System.out.println(message);
	}		

}
