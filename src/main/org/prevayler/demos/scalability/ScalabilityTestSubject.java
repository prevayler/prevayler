package org.prevayler.demos.scalability;

import java.io.PrintStream;

public interface ScalabilityTestSubject {

	public String name();

	public void replaceAllRecords(int records);

	public Object createTestConnection();

	public void reportResourcesUsed(PrintStream out);

}
