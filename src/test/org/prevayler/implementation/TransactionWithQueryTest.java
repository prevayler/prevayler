package org.prevayler.implementation;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.serialization.JavaSerializer;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.foundation.serialization.SkaringaSerializer;
import org.prevayler.foundation.serialization.XStreamSerializer;

import java.io.IOException;

public class TransactionWithQueryTest extends FileIOTest {

	public void testJavaJournal() throws Exception {
		Serializer strategy = new JavaSerializer();

		startAndCrash(strategy);
		recover(strategy);
	}

	public void testXStreamJournal() throws Exception {
		Serializer strategy = new XStreamSerializer();

		startAndCrash(strategy);
		recover(strategy);
	}

	public void NOT_WORKING_testSkaringaJournal() throws Exception {
		Serializer strategy = new SkaringaSerializer();

		startAndCrash(strategy);
		recover(strategy);
	}

	private void startAndCrash(Serializer journalSerializer) throws Exception {
		Prevayler prevayler = createPrevayler(journalSerializer);

		assertEquals("the system first", prevayler.execute(new AppendTransactionWithQuery(" first")));
		assertEquals("the system first second", prevayler.execute(new AppendTransactionWithQuery(" second")));
		assertEquals("the system first second third", prevayler.execute(new AppendTransactionWithQuery(" third")));
		assertEquals("the system first second third", prevayler.prevalentSystem().toString());
		prevayler.close();
	}

	private void recover(Serializer journalSerializer) throws Exception {
		Prevayler prevayler = createPrevayler(journalSerializer);
		assertEquals("the system first second third", prevayler.prevalentSystem().toString());
	}

	private Prevayler createPrevayler(Serializer journalSerializer) throws Exception {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(new StringBuffer("the system"));
		factory.configurePrevalenceDirectory(_testDirectory);
		factory.configureJournalSerializer("journal", journalSerializer);
		return factory.create();
	}

}
