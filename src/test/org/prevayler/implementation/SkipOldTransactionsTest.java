package org.prevayler.implementation;

import org.prevayler.Clock;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.serialization.Serializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

public class SkipOldTransactionsTest extends FileIOTest {

	public void testSkipOldTransactions() throws IOException, ClassNotFoundException {
		Prevayler original = createPrevayler("MyJournal", new MySerializer(false));

		original.execute(new AppendTransaction(" first"));
		original.execute(new AppendTransaction(" second"));

		original.takeSnapshot();

		original.execute(new AppendTransaction(" third"));
		assertEquals("the system first second third", original.prevalentSystem().toString());
		original.close();

		assertEquals("6;systemVersion=1;executionTime=1000002\r\n" +
				" first\r\n" +
				"7;systemVersion=2;executionTime=1000004\r\n" +
				" second\r\n" +
				"6;systemVersion=3;executionTime=1000006\r\n" +
				" third\r\n", journalContents("MyJournal"));

		Prevayler recovered = createPrevayler("MyJournal", new MySerializer(true));
		assertEquals("the system first second third", recovered.prevalentSystem().toString());
	}

	public void testDetectOldJournalSuffix() throws IOException, ClassNotFoundException {
		Prevayler original = createPrevayler("OldJournal", new MySerializer(false));

		original.execute(new AppendTransaction(" first"));
		original.execute(new AppendTransaction(" second"));

		original.takeSnapshot();

		original.execute(new AppendTransaction(" third"));
		assertEquals("the system first second third", original.prevalentSystem().toString());
		original.close();

		assertEquals("6;systemVersion=1;executionTime=1000002\r\n" +
				" first\r\n" +
				"7;systemVersion=2;executionTime=1000004\r\n" +
				" second\r\n" +
				"6;systemVersion=3;executionTime=1000006\r\n" +
				" third\r\n", journalContents("OldJournal"));

		try {
			createPrevayler("NewJournal", new MySerializer(true));
			fail();
		} catch (IOException exception) {
			File journal = new PrevaylerDirectory(_testDirectory).journalFile(1, "OldJournal");
			assertEquals("There are transactions needing to be recovered from " + journal +
					", but only NewJournal files are supported", exception.getMessage());
		}
	}

	public void testAllowOldJournalSuffix() throws IOException, ClassNotFoundException {
		Prevayler original = createPrevayler("OldJournal", new MySerializer(false));

		original.execute(new AppendTransaction(" first"));
		original.execute(new AppendTransaction(" second"));
		original.execute(new AppendTransaction(" third"));
		original.takeSnapshot();

		assertEquals("the system first second third", original.prevalentSystem().toString());
		original.close();

		assertEquals("6;systemVersion=1;executionTime=1000002\r\n" +
				" first\r\n" +
				"7;systemVersion=2;executionTime=1000004\r\n" +
				" second\r\n" +
				"6;systemVersion=3;executionTime=1000006\r\n" +
				" third\r\n", journalContents("OldJournal"));

		Prevayler recovered = createPrevayler("NewJournal", new MySerializer(true));
		assertEquals("the system first second third", recovered.prevalentSystem().toString());
	}

	private Prevayler createPrevayler(String suffix, Serializer journalSerializer)
			throws IOException, ClassNotFoundException {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(new StringBuffer("the system"));
		factory.configurePrevalenceDirectory(_testDirectory);
		factory.configureJournalSerializer(suffix, journalSerializer);
		factory.configureClock(new Clock() {
			private long time = 1000000;

			public Date time() {
				return new Date(++time);
			}
		});
		return factory.create();
	}

	private static class MySerializer implements Serializer {

		private boolean afterSnapshot;

		public MySerializer(boolean afterSnapshot) {
			this.afterSnapshot = afterSnapshot;
		}

		public void writeObject(OutputStream stream, Object object) throws IOException {
			Writer writer = new OutputStreamWriter(stream, "UTF-8");
			AppendTransaction transaction = (AppendTransaction) object;
			writer.write(transaction.toAdd);
			writer.flush();
		}

		public Object readObject(InputStream stream) throws IOException, ClassNotFoundException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			String toAdd = reader.readLine();
			if (afterSnapshot) {
				assertFalse("Shouldn't have recovered transaction from before snapshot",
						toAdd.equals(" first") || toAdd.equals(" second"));
			}
			return new AppendTransaction(toAdd);
		}

	}

}
