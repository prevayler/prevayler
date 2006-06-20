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

public class QueryTestRun extends ScalabilityTestRun<QueryConnection> {

    public QueryTestRun(ScalabilityTestSubject<QueryConnection> subject, int numberOfObjects, int minThreads, int maxThreads) {
        super(subject, numberOfObjects, minThreads, maxThreads);
    }

    @Override protected String name() {
        return "Query Test";
    }

    @Override protected void executeOperation(QueryConnection connection, long operationSequence) {
        connection.queryByName("NAME" + (operationSequence % 10000));
    }

}
