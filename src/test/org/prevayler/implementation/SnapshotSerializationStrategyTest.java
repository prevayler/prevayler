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

public class SnapshotSerializationStrategyTest extends FileIOTest {

	public void testConfigureSnapshotSerializationStrategy() throws IOException, ClassNotFoundException {
		SerializationStrategy strategy = new MySerializationStrategy();

		takeSnapshot(strategy);

		assertEquals("Yes, this is MySerializationStrategy!\n" +
				"the system first second third\n", snapshotContents());

		recover(strategy);
	}

	public void testXStreamSnapshot() throws IOException, ClassNotFoundException {
		SerializationStrategy strategy = new XStreamSerializationStrategy();

		takeSnapshot(strategy);
		recover(strategy);
	}

	public void testSkaringaSnapshot() throws IOException, ClassNotFoundException {
		SerializationStrategy strategy = new SkaringaSerializationStrategy();

		takeSnapshot(strategy);
		recover(strategy);
	}

	private void takeSnapshot(SerializationStrategy journalSerializationStrategy)
			throws IOException, ClassNotFoundException {
		Prevayler prevayler = createPrevayler(journalSerializationStrategy);

		prevayler.execute(new AppendTransaction(" first"));
		prevayler.execute(new AppendTransaction(" second"));
		prevayler.execute(new AppendTransaction(" third"));
		assertEquals("the system first second third", prevayler.prevalentSystem().toString());

		prevayler.takeSnapshot();
		prevayler.close();
	}

	private void recover(SerializationStrategy snapshotSerializationStrategy)
			throws IOException, ClassNotFoundException {
		Prevayler prevayler = createPrevayler(snapshotSerializationStrategy);
		assertEquals("the system first second third", prevayler.prevalentSystem().toString());
	}

	private Prevayler createPrevayler(SerializationStrategy snapshotSerializationStrategy)
			throws IOException, ClassNotFoundException {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(new StringBuffer("the system"));
		factory.configurePrevalenceDirectory(_testDirectory);
		factory.configureSnapshotSerializationStrategy("snapshot", snapshotSerializationStrategy);
		return factory.create();
	}

	private String snapshotContents() throws IOException {
		File snapshot = new File(_testDirectory).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".snapshot");
			}
		})[0];

		FileReader file = new FileReader(snapshot);
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
					StringBuffer system = (StringBuffer) object;
					writer.write("Yes, this is MySerializationStrategy!\n");
					writer.write(system.toString());
					writer.write('\n');
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
					String prolog = reader.readLine();
					if ("Yes, this is MySerializationStrategy!".equals(prolog)) {
						String contents = reader.readLine();
						return new StringBuffer(contents);
					} else {
						throw new AssertionFailedError("got prolog=" + prolog);
					}
				}
			};
		}

	}

}
