//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import junit.framework.AssertionFailedError;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Transaction;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.serialization.AbstractSerializationStrategy;
import org.prevayler.foundation.serialization.Deserializer;
import org.prevayler.foundation.serialization.Serializer;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.Date;


public class PrevaylerFactoryTest extends FileIOTest {

	private static final Serializable POJO = new Serializable() {
	};

	public void testTransientPrevaylerCreation() {
		Prevayler prevayler = PrevaylerFactory.createTransientPrevayler(POJO);
		assertEquals(POJO, prevayler.prevalentSystem());
	}

	public void testSnapshotPrevaylerCreation() throws IOException, ClassNotFoundException {
		Prevayler prevayler = PrevaylerFactory.createPrevayler(POJO, _testDirectory);
		assertEquals(POJO, prevayler.prevalentSystem());
	}

	public void testCheckpointPrevaylerCreation() throws IOException, ClassNotFoundException {
		Prevayler prevayler = PrevaylerFactory.createCheckpointPrevayler(POJO, _testDirectory);
		assertEquals(POJO, prevayler.prevalentSystem());
	}

	public void testConfigureJournalSerializationStrategy() throws IOException, ClassNotFoundException {
		Prevayler prevayler = createMyPrevayler();

		prevayler.execute(new MyTransaction(" first"));
		prevayler.execute(new MyTransaction(" second"));
		prevayler.execute(new MyTransaction(" third"));
		assertEquals("the system first second third", ((StringBuffer) prevayler.prevalentSystem()).toString());
		prevayler.close();

		File journal = new File(_testDirectory).listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.endsWith(".journal");
			}
		})[0];
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(journal), "UTF-8"));
		assertEquals("TransactionTimestamp", reader.readLine());
		assertEquals(" first", reader.readLine());
		assertEquals("TransactionTimestamp", reader.readLine());
		assertEquals(" second", reader.readLine());
		assertEquals("TransactionTimestamp", reader.readLine());
		assertEquals(" third", reader.readLine());
		assertNull(reader.readLine());
		reader.close();

		prevayler = createMyPrevayler();
		assertEquals("the system first second third", ((StringBuffer) prevayler.prevalentSystem()).toString());
	}

	private Prevayler createMyPrevayler() throws IOException, ClassNotFoundException {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(new StringBuffer("the system"));
		factory.configurePrevalenceDirectory(_testDirectory);
		factory.configureJournalSerializationStrategy(new MySerializationStrategy());
		return factory.create();
	}

	private static class MyTransaction implements Transaction {
		String toAdd;

		public MyTransaction(String toAdd) {
			this.toAdd = toAdd;
		}

		public void executeOn(Object prevalentSystem, Date executionTime) {
			StringBuffer system = (StringBuffer) prevalentSystem;
			system.append(toAdd);
		}
	}

	private static class MySerializationStrategy extends AbstractSerializationStrategy {
		public Serializer createSerializer(final OutputStream stream) throws IOException {
			final Writer writer = new OutputStreamWriter(stream, "UTF-8");
			return new Serializer() {
				public void writeObject(Object object) throws IOException {
					if (object instanceof TransactionTimestamp) {
						TransactionTimestamp timestamp = (TransactionTimestamp) object;
						MyTransaction transaction = (MyTransaction) timestamp.transaction();
						writer.write("TransactionTimestamp\n");
						writer.write(transaction.toAdd);
						writer.write('\n');
					} else {
						MyTransaction transaction = (MyTransaction) object;
						writer.write("MyTransaction\n");
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
						return new TransactionTimestamp(new MyTransaction(toAdd), new Date(87527359273L));
					} else if ("MyTransaction".equals(type)) {
						String toAdd = reader.readLine();
						return new MyTransaction(toAdd);
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
