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

import java.io.PrintStream;

public interface ScalabilityTestSubject<C> {

    public String name();

    public void replaceAllRecords(int records);

    public C createTestConnection();

    public void reportResourcesUsed(PrintStream out);

}
