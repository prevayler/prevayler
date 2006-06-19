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
import org.prevayler.demos.scalability.ScalabilityTestSubject;

abstract class PrevaylerScalabilitySubject implements ScalabilityTestSubject {

    protected Prevayler prevayler;

    {
        System.gc();
    }

    public String name() {
        return "Prevayler";
    }

    public void replaceAllRecords(int records) {
        try {

            prevayler.execute(new AllRecordsReplacement(records));

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Unexpected Exception: " + ex);
        }
    }

}
