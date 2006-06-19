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

import org.prevayler.Prevayler;
import org.prevayler.demos.scalability.Record;
import org.prevayler.demos.scalability.TransactionConnection;

class PrevaylerTransactionConnection implements TransactionConnection {

    private final Prevayler prevayler;

    PrevaylerTransactionConnection(Prevayler prevayler) {
        this.prevayler = prevayler;
    }

    public void performTransaction(Record recordToInsert, Record recordToUpdate, long idToDelete) {
        try {

            prevayler.execute(new TestTransaction(recordToInsert, recordToUpdate, idToDelete));

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Unexpected Exception: " + ex);
        }
    }
}
