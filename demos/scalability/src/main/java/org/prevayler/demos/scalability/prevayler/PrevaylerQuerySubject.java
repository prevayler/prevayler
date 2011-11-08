package org.prevayler.demos.scalability.prevayler;

import java.io.File;
import java.io.PrintStream;

import org.prevayler.PrevaylerFactory;

public class PrevaylerQuerySubject extends PrevaylerScalabilitySubject {

	static final String PREVALENCE_BASE = "QueryTest";

	public PrevaylerQuerySubject() throws Exception {
		if (new File(PREVALENCE_BASE).exists()) PrevalenceTest.delete(PREVALENCE_BASE);
		PrevaylerFactory<QuerySystem> factory = new PrevaylerFactory<QuerySystem>();
		factory.configurePrevalentSystem(new QuerySystem());
		factory.configurePrevalenceDirectory(PREVALENCE_BASE);
		prevayler = factory.create();
	}


	public Object createTestConnection() {
		return new PrevaylerQueryConnection((QuerySystem)prevayler.prevalentSystem());
	}

	public void reportResourcesUsed(PrintStream out) {
	}

}
