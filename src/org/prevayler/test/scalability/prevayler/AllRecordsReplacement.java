// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.test.scalability.prevayler;

import org.prevayler.*;
import org.prevayler.test.scalability.RecordIterator;
import java.io.Serializable;

class AllRecordsReplacement implements Command {

	private final RecordIterator newRecords;

	AllRecordsReplacement(RecordIterator newRecords) {
		this.newRecords = newRecords;
	}

	public Serializable execute(PrevalentSystem system) {
		((ScalabilitySystem)system).replaceAllRecords(newRecords);
		return null;
	}
}
