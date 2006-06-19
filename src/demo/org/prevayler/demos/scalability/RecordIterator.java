// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.scalability;

import java.io.Serializable;
import java.util.Random;

/**
 * Generates Record objects with ids from 0 to numberOfRecords - 1.
 */
public class RecordIterator implements Serializable {

    private static final long serialVersionUID = 4278565982042966467L;

    private int nextRecordId = 0;

    private final int numberOfRecords;

    private final Random _random = new Random(0);

    public RecordIterator(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public boolean hasNext() {
        return nextRecordId < numberOfRecords;
    }

    public Record next() {
        indicateProgress();
        return new Record(nextRecordId++, _random);
    }

    private void indicateProgress() {
        if (nextRecordId == 0) {
            out("Creating " + numberOfRecords + " objects...");
            return;
        }
        if (nextRecordId % 100000 == 0)
            out("" + nextRecordId + "...");
    }

    static private void out(Object message) {
        System.out.println(message);
    }
}
