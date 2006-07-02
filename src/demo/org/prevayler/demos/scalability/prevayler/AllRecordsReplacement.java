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

import org.prevayler.GenericTransaction;
import org.prevayler.PrevalenceContext;
import org.prevayler.demos.scalability.RecordIterator;

import java.io.Serializable;

class AllRecordsReplacement implements GenericTransaction<ScalabilitySystem, Void, RuntimeException>, Serializable {

    private static final long serialVersionUID = 6283032417365727408L;

    private final int _records;

    AllRecordsReplacement(int records) {
        _records = records;
    }

    public Void executeOn(ScalabilitySystem system, @SuppressWarnings("unused") PrevalenceContext prevalenceContext) {
        system.replaceAllRecords(new RecordIterator(_records));
        return null;
    }

}
