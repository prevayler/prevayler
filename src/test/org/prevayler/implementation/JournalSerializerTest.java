package org.prevayler.implementation;

import junit.framework.AssertionFailedError;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.foundation.serialization.XStreamSerializer;

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

public class JournalSerializerTest extends FileIOTest {

	public void testConfigureJournalSerializationStrategy() throws IOException, ClassNotFoundException {
		Serializer strategy = new MySerializer();

		startAndCrash(strategy);

		assertEquals("1C\r\n" +
				"TransactionTimestamp\n" +
				" first\n" +
				"\r\n" +
				"1D\r\n" +
				"TransactionTimestamp\n" +
				" second\n" +
				"\r\n" +
				"1C\r\n" +
				"TransactionTimestamp\n" +
				" third\n" +
				"\r\n", journalContents());

		recover(strategy);
	}

	public void testXStreamJournal() throws IOException, ClassNotFoundException {
		Serializer strategy = new XStreamSerializer();

		startAndCrash(strategy);
		recover(strategy);
	}

	private void startAndCrash(Serializer journalSerializer)
			throws IOException, ClassNotFoundException {
		Prevayler prevayler = createPrevayler(journalSerializer);

		prevayler.execute(new AppendTransaction(" first"));
		prevayler.execute(new AppendTransaction(" second"));
		prevayler.execute(new AppendTransaction(" third"));
		assertEquals("the system first second third", prevayler.prevalentSystem().toString());
		prevayler.close();
	}

	private void recover(Serializer journalSerializer)
			throws IOException, ClassNotFoundException {
		Prevayler prevayler = createPrevayler(journalSerializer);
		assertEquals("the system first second third", prevayler.prevalentSystem().toString());
	}

	private Prevayler createPrevayler(Serializer journalSerializer)
			throws IOException, ClassNotFoundException {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(new StringBuffer("the system"));
		factory.configurePrevalenceDirectory(_testDirectory);
		factory.configureJournalSerializer(journalSerializer);
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

	private static class MySerializer implements Serializer {

		public void writeObject(OutputStream stream, Object object) throws IOException {
			Writer writer = new OutputStreamWriter(stream, "UTF-8");
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
			writer.flush();
		}

		public Object readObject(InputStream stream) throws IOException, ClassNotFoundException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
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

	}

}
