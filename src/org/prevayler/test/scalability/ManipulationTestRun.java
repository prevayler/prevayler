// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.test.scalability;

import java.util.*;
import java.math.BigDecimal;

/** Tests insert, update and delete scalability.
*/
public class ManipulationTestRun extends ScalabilityTestRun {

	public ManipulationTestRun(ScalabilityTestSubject subject, int minThreads, int maxThreads) {
		super(subject, minThreads, maxThreads);
	}


	protected String name() {
		return "Manipulation Test";
	}


	/**
	* Deletes records from id 0000000 to 0499999.
	* Updates records from id 0500000 to 0999999.
	* Inserts records from id 1000000 to 1500000.
	* Every time 500000 operations have completed, all ranges are shifted up by 500000.
	*/
	protected void executeOperation(Object connection, long operationSequence) {
		Record recordToInsert = new Record(operationSequence + 1000000);
		long idToDelete = spreadId(operationSequence);
		Record recordToUpdate = new Record(idToDelete + 500000);

		((ManipulationConnection)connection).performTransaction(recordToInsert, recordToUpdate, idToDelete);
	}


	/** Spreads out the id values so that deletes and updates are not done contiguously.
	*/
	static private long spreadId(long id) {
		return (id / 500000) * 500000
			+ ((id * 16807) % 500000);   //16807 == 7 * 7 * 7 * 7 * 7. 16807 is relatively prime to 500000. This guarantees that all ids in the range will be covered.
	}
}
