// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.test.scalability;

import java.io.Serializable;

/** Generates one million Record objects with ids from 0 to 999999.
*/
public class RecordIterator implements Serializable {

	private int remainingRecords = 1000000;


	public boolean hasNext() {
		return remainingRecords != 0;
	}


	public Record next() {
		indicateProgress();

		remainingRecords--;
		return new Record(remainingRecords);
	}


	private void indicateProgress() {
		if (remainingRecords == 1000000) {
			out("Creating one million records...");
			return;
		}
		if (remainingRecords % 100000 == 0) out("" + (1000000 - remainingRecords) + "...");
	}


	static private void out(Object message) {
		System.out.println(message);
	}
}
