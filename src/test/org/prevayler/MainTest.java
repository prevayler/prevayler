//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler;

import junit.textui.TestRunner;


/** The main class for testing Prevayler.
*/
public class MainTest {

	static public void main(String[] args) throws Exception {
		TestRunner.run(AllTestSuite.suite()); 
	}

}
