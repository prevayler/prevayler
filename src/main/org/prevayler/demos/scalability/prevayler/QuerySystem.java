// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.scalability.prevayler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.prevayler.demos.scalability.Record;
import org.prevayler.demos.scalability.RecordIterator;

class QuerySystem implements ScalabilitySystem {

	private Map recordsByName = new HashMap();


	public List queryByName(String name) {
		return (List)recordsByName.get(name);
	}


	public void replaceAllRecords(RecordIterator newRecords) {
		recordsByName.clear();

		while (newRecords.hasNext()) {
			put(newRecords.next());
		}

		makeReadOnly();
	}


	private void put(Record newRecord) {
		List records = queryByName(newRecord.getName());
		if (records == null) {
			records = new ArrayList();
			recordsByName.put(newRecord.getName(), records);
		}
		
		records.add(newRecord);
	}


	/** This is necessary so that the clients cannot alter the Lists they receive as query results.
	*/
	private void makeReadOnly() {
		Iterator entries = recordsByName.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry)entries.next();
			entry.setValue(Collections.unmodifiableList((List)entry.getValue()));
		}
	}
}
