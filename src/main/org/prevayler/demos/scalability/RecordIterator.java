// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.scalability;

import java.io.Serializable;

/** Generates Record objects with ids from 0 to numberOfRecords - 1.
*/
public class RecordIterator implements Serializable {

	private final int numberOfRecords;
	private int nextRecordId = 0;


	RecordIterator(int numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

	public boolean hasNext() {
		return nextRecordId < numberOfRecords;
	}

	public Record next() {
		indicateProgress();
		return new Record(nextRecordId++);
	}

	private void indicateProgress() {
		if (nextRecordId == 0) {
			out("Creating " + numberOfRecords + " objects...");
			return;
		}
		if (nextRecordId % 100000 == 0) out("" + nextRecordId + "...");
	}


	static private void out(Object message) {
		System.out.println(message);
	}
}
