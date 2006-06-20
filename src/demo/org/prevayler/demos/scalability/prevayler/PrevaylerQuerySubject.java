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

import org.prevayler.PrevaylerFactory;
import org.prevayler.demos.scalability.QueryConnection;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class PrevaylerQuerySubject extends PrevaylerScalabilitySubject<QuerySystem, QueryConnection> {

    static final String PREVALENCE_BASE = "QueryTest";

    public PrevaylerQuerySubject() throws IOException {
        if (new File(PREVALENCE_BASE).exists()) {
            PrevalenceTest.delete(PREVALENCE_BASE);
        }
        PrevaylerFactory<QuerySystem> factory = new PrevaylerFactory<QuerySystem>();
        factory.configurePrevalentSystem(new QuerySystem());
        factory.configurePrevalenceDirectory(PREVALENCE_BASE);
        factory.configureTransactionFiltering(false);
        prevayler = factory.create();
    }

    public QueryConnection createTestConnection() {
        return new PrevaylerQueryConnection(prevayler.prevalentSystem());
    }

    public void reportResourcesUsed(@SuppressWarnings("unused") PrintStream out) {
    }

}
