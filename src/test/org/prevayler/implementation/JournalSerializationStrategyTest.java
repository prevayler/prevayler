package org.prevayler.implementation;

import junit.framework.AssertionFailedError;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.serialization.AbstractSerializationStrategy;
import org.prevayler.foundation.serialization.Deserializer;
import org.prevayler.foundation.serialization.SerializationStrategy;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.foundation.serialization.SkaringaSerializationStrategy;
import org.prevayler.foundation.serialization.XStreamSerializationStrategy;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

public class JournalSerializationStrategyTest extends FileIOTest {

	public void testConfigureJournalSerializationStrategy() throws IOException, ClassNotFoundException {
		SerializationStrategy strategy = new MySerializationStrategy();

		startAndCrash(strategy);

		assertEquals("TransactionTimestamp\n" +
				" first\n" +
				"TransactionTimestamp\n" +
				" second\n" +
				"TransactionTimestamp\n" +
				" third\n", journalContents());

		recover(strategy);
	}

	public void testXStreamJournal() throws IOException, ClassNotFoundException {
		SerializationStrategy strategy = new XStreamSerializationStrategy();

		startAndCrash(strategy);
		recover(strategy);
	}

	public void NOT_WORKING_YET_testSkaringaJournal() throws IOException, ClassNotFoundException {
		SerializationStrategy strategy = new SkaringaSerializationStrategy();

		startAndCrash(strategy);
		recover(strategy);
	}

	private void startAndCrash(SerializationStrategy journalSerializationStrategy)
			throws IOException, ClassNotFoundException {
		Prevayler prevayler = createPrevayler(journalSerializationStrategy);

		prevayler.execute(new AppendTransaction(" first"));
		prevayler.execute(new AppendTransaction(" second"));
		prevayler.execute(new AppendTransaction(" third"));
		assertEquals("the system first second third", prevayler.prevalentSystem().toString());
		prevayler.close();
	}

	private void recover(SerializationStrategy journalSerializationStrategy)
			throws IOException, ClassNotFoundException {
		Prevayler prevayler = createPrevayler(journalSerializationStrategy);
		assertEquals("the system first second third", prevayler.prevalentSystem().toString());
	}

	private Prevayler createPrevayler(SerializationStrategy journalSerializationStrategy)
			throws IOException, ClassNotFoundException {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(new StringBuffer("the system"));
		factory.configurePrevalenceDirectory(_testDirectory);
		factory.configureJournalSerializationStrategy(journalSerializationStrategy);
		return factory.create();
	}

	private String journalContents() throws IOException {
		File journal = new File(_testDirectory).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".journal");
			}
		})[0];

		FileReader file = new FileReader(journal);
		StringWriter string = new StringWriter();

		int n;
		char[] c = new char[1024];
		while ((n = file.read(c)) != -1) {
			string.write(c, 0, n);
		}

		file.close();

		return string.toString();
	}

	private static class MySerializationStrategy extends AbstractSerializationStrategy {

		public Serializer createSerializer(final OutputStream stream) throws IOException {
			final Writer writer = new OutputStreamWriter(stream, "UTF-8");
			return new Serializer() {
				public void writeObject(Object object) throws IOException {
					if (object instanceof TransactionTimestamp) {
						TransactionTimestamp timestamp = (TransactionTimestamp) object;
						AppendTransaction transaction = (AppendTransaction) timestamp.transaction();
						writer.write("TransactionTimestamp\n");
						writer.write(transaction.toAdd);
						writer.write('\n');
					} else {
						AppendTransaction transaction = (AppendTransaction) object;
						writer.write("AppendTransaction\n");
						writer.write(transaction.toAdd);
						writer.write('\n');
					}
				}

				public void flush() throws IOException {
					writer.flush();
				}
			};
		}

		public Deserializer createDeserializer(final InputStream stream) throws IOException {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			return new Deserializer() {
				public Object readObject() throws IOException {
					String type = reader.readLine();
					if ("TransactionTimestamp".equals(type)) {
						String toAdd = reader.readLine();
						return new TransactionTimestamp(new AppendTransaction(toAdd), new Date(87527359273L));
					} else if ("AppendTransaction".equals(type)) {
						String toAdd = reader.readLine();
						return new AppendTransaction(toAdd);
					} else if (type == null) {
						throw new EOFException();
					} else {
						throw new AssertionFailedError("got type=" + type);
					}
				}
			};
		}

	}

}
