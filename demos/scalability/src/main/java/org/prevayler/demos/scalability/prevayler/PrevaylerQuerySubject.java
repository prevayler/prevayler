package org.prevayler.demos.scalability.prevayler;

import java.io.File;
import java.io.PrintStream;

import org.prevayler.PrevaylerFactory;
//import org.prevayler.implementation.PrevalenceTest;

public class PrevaylerQuerySubject extends PrevaylerScalabilitySubject {

	static final String PREVALENCE_BASE = "QueryTest";

	public PrevaylerQuerySubject() throws java.io.IOException, ClassNotFoundException {
		if (new File(PREVALENCE_BASE).exists()) PrevalenceTest.delete(PREVALENCE_BASE);
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(new QuerySystem());
		factory.configurePrevalenceDirectory(PREVALENCE_BASE);
		factory.configureTransactionFiltering(false);
		prevayler = factory.create();
	}


	public Object createTestConnection() {
		return new PrevaylerQueryConnection((QuerySystem)prevayler.prevalentSystem());
	}

	public void reportResourcesUsed(PrintStream out) {
	}

}
