package org.prevayler.demos.scalability.prevayler;

import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.serialization.Serializer;

import java.io.File;
import java.io.PrintStream;


public class PrevaylerTransactionSubject extends PrevaylerScalabilitySubject {
	
	private final String _journalDirectory;
	private final String _journalSerializer;

	public PrevaylerTransactionSubject(String journalDirectory, String journalSerializer) throws Exception {
		_journalDirectory = journalDirectory;
		_journalSerializer = journalSerializer;
		if (new File(_journalDirectory).exists()) PrevalenceTest.delete(_journalDirectory);
		initializePrevayler();
	}

	public Object createTestConnection() {
		return new PrevaylerTransactionConnection(prevayler);
	}

	public void reportResourcesUsed(PrintStream out) {
		int totalSize = 0;
		File[] files = new File(_journalDirectory).listFiles();
		for (int i = 0; i < files.length; i++) {
			totalSize += files[i].length();
		}
		out.println("Disk space used: " + totalSize);
	}

	public boolean isConsistent() throws Exception {
		int expectedResult = prevayler.prevalentSystem().hashCode();
		initializePrevayler();  //Will reload all transactions from the log files.
		return prevayler.prevalentSystem().hashCode() == expectedResult;
	}

	private void initializePrevayler() throws Exception {
		PrevaylerFactory<TransactionSystem> factory = new PrevaylerFactory<TransactionSystem>();
		factory.configurePrevalentSystem(new TransactionSystem());
		factory.configurePrevalenceDirectory(_journalDirectory);
		factory.configureJournalSerializer("journal", (Serializer) Class.forName(_journalSerializer).newInstance());
		factory.configureTransactionFiltering(false);
		prevayler = factory.create();  //No snapshot is generated by the test.
	}
	
}
