package org.prevayler.demos.scalability.prevayler;

import java.io.File;

import org.prevayler.PrevaylerFactory;
//import org.prevayler.implementation.PrevalenceTest;

public class PrevaylerQuerySubject extends PrevaylerScalabilitySubject {

	static final String PREVALENCE_BASE = "QueryTest";

	public PrevaylerQuerySubject() throws java.io.IOException, ClassNotFoundException {
		if (new File(PREVALENCE_BASE).exists()) PrevalenceTest.delete(PREVALENCE_BASE);
		prevayler = PrevaylerFactory.createPrevayler(new QuerySystem(), PREVALENCE_BASE);
	}


	public Object createTestConnection() {
		return new PrevaylerQueryConnection((QuerySystem)prevayler.prevalentSystem());
	}
}
