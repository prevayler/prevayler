package org.prevayler.implementation;

import org.prevayler.Clock;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.serialization.Serializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

public class HideTransactionWithQueryExecuterFromSerializersTest extends FileIOTest {

	public void testHideTransactionWithQueryExecuterFromSerializers() throws Exception {
		Serializer strategy = new MySerializer();

		startAndCrash(strategy);

		assertEquals("6;timestamp=1000002\r\n" +
				" first\r\n" +
				"7;timestamp=1000004\r\n" +
				" second\r\n" +
				"6;timestamp=1000006\r\n" +
				" third\r\n", journalContents("MyJournal"));

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
		factory.configureJournalSerializer("MyJournal", journalSerializer);
		factory.configureClock(new Clock() {
			private long time = 1000000;

			public Date time() {
				return new Date(++time);
			}
		});
		return factory.create();
	}

	private static class MySerializer implements Serializer {

		public void writeObject(OutputStream stream, Object object) throws IOException {
			Writer writer = new OutputStreamWriter(stream, "UTF-8");
			AppendTransactionWithQuery transaction = (AppendTransactionWithQuery) object;
			writer.write(transaction.toAdd);
			writer.flush();
		}

		public Object readObject(InputStream stream) throws IOException, ClassNotFoundException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			return new AppendTransactionWithQuery(reader.readLine());
		}

	}

}
