package org.prevayler.implementation;

import org.prevayler.Clock;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.serialization.Serializer;

import java.io.BufferedReader;
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

public class SkipOldTransactionsTest extends FileIOTest {

	public void testConfigureJournalSerializationStrategy() throws IOException, ClassNotFoundException {
		Prevayler original = createPrevayler(new MySerializer(false));

		original.execute(new AppendTransaction(" first"));
		original.execute(new AppendTransaction(" second"));

		original.takeSnapshot();

		original.execute(new AppendTransaction(" third"));
		assertEquals("the system first second third", original.prevalentSystem().toString());
		original.close();

		assertEquals("6;timestamp=1000002\r\n" +
				" first\r\n" +
				"7;timestamp=1000004\r\n" +
				" second\r\n" +
				"6;timestamp=1000006\r\n" +
				" third\r\n", journalContents());

		Prevayler recovered = createPrevayler(new MySerializer(true));
		assertEquals("the system first second third", recovered.prevalentSystem().toString());
	}

	private Prevayler createPrevayler(Serializer journalSerializer)
			throws IOException, ClassNotFoundException {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(new StringBuffer("the system"));
		factory.configurePrevalenceDirectory(_testDirectory);
		factory.configureJournalSerializer(journalSerializer);
		factory.configureClock(new Clock() {
			private long time = 1000000;

			public Date time() {
				return new Date(++time);
			}
		});
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
