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

import java.io.File;
import java.io.PrintStream;

//import org.prevayler.implementation.PrevalenceTest;

public class PrevaylerQuerySubject extends PrevaylerScalabilitySubject {

    static final String PREVALENCE_BASE = "QueryTest";

    public PrevaylerQuerySubject() throws java.io.IOException, ClassNotFoundException {
        if (new File(PREVALENCE_BASE).exists())
            PrevalenceTest.delete(PREVALENCE_BASE);
        PrevaylerFactory factory = new PrevaylerFactory();
        factory.configurePrevalentSystem(new QuerySystem());
        factory.configurePrevalenceDirectory(PREVALENCE_BASE);
        factory.configureTransactionFiltering(false);
        prevayler = factory.create();
    }

    public Object createTestConnection() {
        return new PrevaylerQueryConnection((QuerySystem) prevayler.prevalentSystem());
    }

    public void reportResourcesUsed(PrintStream out) {
    }

}
