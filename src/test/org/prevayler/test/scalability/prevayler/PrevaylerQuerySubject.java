// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.test.scalability.prevayler;

import org.prevayler.test.old.PersistenceTest;
import org.prevayler.util.PrevaylerFactory;

public class PrevaylerQuerySubject extends PrevaylerScalabilitySubject {

	static final String LOG_DIRECTORY = "QueryTest";


	public PrevaylerQuerySubject() throws java.io.IOException, ClassNotFoundException {
		PersistenceTest.deletePrevalenceFiles(LOG_DIRECTORY);

		prevayler = PrevaylerFactory.createSnapshotPrevayler(new QuerySystem(), LOG_DIRECTORY);
	}


	public Object createTestConnection() {
		return new PrevaylerQueryConnection((QuerySystem)prevayler.prevalentSystem());
	}
}
