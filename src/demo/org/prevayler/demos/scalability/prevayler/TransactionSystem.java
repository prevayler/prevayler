// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.scalability.prevayler;

import org.prevayler.demos.scalability.Record;
import org.prevayler.demos.scalability.RecordIterator;

import java.util.HashMap;
import java.util.Map;

class TransactionSystem implements ScalabilitySystem {

    private static final long serialVersionUID = 461535927650714306L;

    private final Map recordsById = new HashMap();

    public void performTransaction(Record recordToInsert, Record recordToUpdate, long idToDelete) {
        synchronized (recordsById) {
            put(recordToInsert);
            put(recordToUpdate);
            recordsById.remove(new Long(idToDelete));
        }
    }

    private Object put(Record newRecord) {
        Object key = new Long(newRecord.getId());
        return recordsById.put(key, newRecord);
    }

    public void replaceAllRecords(RecordIterator newRecords) {
        recordsById.clear();

        while (newRecords.hasNext()) {
            put(newRecords.next());
        }
    }

    public int hashCode() {
        return recordsById.hashCode();
    }
}
