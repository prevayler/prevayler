package org.prevayler.demos.scalability;

public interface ScalabilityTestSubject {

	public String name();

	public void replaceAllRecords(int records);

	public Object createTestConnection();
}
