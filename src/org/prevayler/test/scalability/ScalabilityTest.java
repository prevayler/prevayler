// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.test.scalability;

import org.prevayler.test.scalability.gui.ScalabilityTestFrame;

public class ScalabilityTest {

	static public void main(String[] args) {

		try {


//Temporary code: (it will no longer be necessary once the Scalability Test GUI is ready).
new ManipulationTestRun(
	new org.prevayler.test.scalability.prevayler.PrevaylerManipulationSubject(logDirectories()),1,3
);

new QueryTestRun(
	new org.prevayler.test.scalability.prevayler.PrevaylerQuerySubject(),1,3
);

new ManipulationTestRun(
	new org.prevayler.test.scalability.jdbc.JDBCManipulationSubject("jdbcDriverClassName", "connectionURL", "user", "password"),1,3
);

new QueryTestRun(
	new org.prevayler.test.scalability.jdbc.JDBCQuerySubject("jdbcDriverClassName", "connectionURL", "user", "password"),1,3
);
//End of temporary code.




			new ScalabilityTestFrame();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}



//Temporary code: (it will no longer be necessary once the Scalability Test GUI is ready).
static private String[] logDirectories() {
	String[] logDirectories = new String[10];
	java.util.Arrays.fill(logDirectories, "PrevalenceBase");
	return logDirectories;
}
//End of temporary code.


}
