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

import org.prevayler.Transaction;
import org.prevayler.demos.scalability.Record;

import java.util.Date;

class TestTransaction implements Transaction {

    private static final long serialVersionUID = -2634307328586761351L;

    private final Record recordToInsert;

    private final Record recordToUpdate;

    private final long idToDelete;

    TestTransaction(Record recordToInsert, Record recordToUpdate, long idToDelete) {
        this.recordToInsert = recordToInsert;
        this.recordToUpdate = recordToUpdate;
        this.idToDelete = idToDelete;
    }

    public void executeOn(Object system, Date ignored) {
        ((TransactionSystem) system).performTransaction(recordToInsert, recordToUpdate, idToDelete);
    }
}
