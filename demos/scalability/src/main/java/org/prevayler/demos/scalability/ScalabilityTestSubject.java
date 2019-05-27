package org.prevayler.demos.scalability;

import java.io.PrintStream;

public interface ScalabilityTestSubject<C> {

  public String name();

  public void replaceAllRecords(int records);

  public C createTestConnection();

  public void reportResourcesUsed(PrintStream out);

}
