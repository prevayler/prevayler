// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.test;

import java.util.*;

/** The main class for testing Prevayler.
*/
public class FullTest {

    static public void main(String[] args) {
	try {
	    header("Persistence Test...");
	    PersistenceTest.run();

	    header("S U C C E S S ! ! !  Can't you think of any more tests?");

	} catch (Exception e) {
	    e.printStackTrace();
	}
	}

    static private void header(String head) {
	out("");
	out("");
	out("> > > " + head);
	}

    static private void out(Object obj) {
	System.out.println(obj);
	}

}
