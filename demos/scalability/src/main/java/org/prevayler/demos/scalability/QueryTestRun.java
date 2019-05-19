package org.prevayler.demos.scalability;

public class QueryTestRun extends ScalabilityTestRun<QueryConnection> {

  public QueryTestRun(ScalabilityTestSubject<QueryConnection> subject, int numberOfObjects, int minThreads, int maxThreads) {
    super(subject, numberOfObjects, minThreads, maxThreads);
  }


  protected String name() {
    return "Query Test";
  }


  protected void executeOperation(QueryConnection connection, long operationSequence) {

    connection.queryByName("NAME" + (operationSequence % 10000));

  }
}
