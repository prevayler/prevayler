package org.prevayler.demos.scalability.prevayler;

import org.prevayler.PrevaylerFactory;
import org.prevayler.demos.scalability.QueryConnection;

import java.io.File;
import java.io.PrintStream;

public class PrevaylerQuerySubject extends PrevaylerScalabilitySubject<QuerySystem, QueryConnection> {

  static final String PREVALENCE_BASE = "QueryTest";

  public PrevaylerQuerySubject() throws Exception {
    if (new File(PREVALENCE_BASE).exists()) PrevalenceTest.delete(PREVALENCE_BASE);
    PrevaylerFactory<QuerySystem> factory = new PrevaylerFactory<QuerySystem>();
    factory.configurePrevalentSystem(new QuerySystem());
    factory.configurePrevalenceDirectory(PREVALENCE_BASE);
    prevayler = factory.create();
  }


  public QueryConnection createTestConnection() {
    return new PrevaylerQueryConnection(prevayler.prevalentSystem());
  }

  public void reportResourcesUsed(PrintStream out) {
  }

}
