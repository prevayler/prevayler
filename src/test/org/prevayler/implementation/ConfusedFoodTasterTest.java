package org.prevayler.implementation;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Transaction;
import org.prevayler.foundation.Cool;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.serialization.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class ConfusedFoodTasterTest extends FileIOTest {

	private static String _executions;

	private static synchronized void clearExecutions() {
		_executions = "";
	}

	private static synchronized void addExecution(String message) {
		_executions = _executions + message + "\n";
		ConfusedFoodTasterTest.class.notifyAll();
	}

	private static synchronized boolean didExecute(String message) {
		return _executions.indexOf(message) != -1;
	}

	private static synchronized void waitFor(String message) {
		while (!didExecute(message)) {
			Cool.wait(ConfusedFoodTasterTest.class);
		}
	}

	private Prevayler _prevayler;

	public void testConfusion() throws IOException, ClassNotFoundException, InterruptedException {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem("ignored");
		factory.configurePrevalenceDirectory(_testDirectory);
		factory.configureJournalSerializer("MyJournal", new ConfusingSerializer());

		_prevayler = factory.create();

		clearExecutions();

		new Thread() {
			public void run() {
				_prevayler.execute(new FirstTransaction());
			}
		}.start();

		waitFor("first was tasted");

		try {
			_prevayler.execute(new SecondTransaction());
			fail();
		} catch (RuntimeException e) {
			assertEquals("I taste bad!", e.getMessage());
		}

		new Thread() {
			public void run() {
				_prevayler.execute(new ThirdTransaction());
			}
		}.start();

		Thread.sleep(1000);

		assertFalse(didExecute("third was tasted"));

		addExecution("go ahead with first");

		waitFor("first was kinged");
		waitFor("third was kinged");
	}

	protected void tearDown() throws Exception {
		if (_prevayler != null) _prevayler.close();
	}

	private static class FirstTransaction implements Transaction {

		public void executeOn(Object prevalentSystem, Date executionTime) {
			if (didExecute("first was tasted")) {
				addExecution("first was kinged");
			} else {
				addExecution("first was tasted");
			}
		}

	}

	private static class SecondTransaction implements Transaction {

		public void executeOn(Object prevalentSystem, Date executionTime) {
			throw new RuntimeException("I taste bad!");
		}

	}

	private static class ThirdTransaction implements Transaction {

		public void executeOn(Object prevalentSystem, Date executionTime) {
			if (didExecute("third was tasted")) {
				addExecution("third was kinged");
			} else {
				addExecution("third was tasted");
			}
		}

	}

	private static class ConfusingSerializer implements Serializer {

		public void writeObject(OutputStream stream, Object object) throws IOException {
			if (object instanceof FirstTransaction) {
				if (didExecute("first was tasted")) {
					// Hang during journal serialization so that this transaction doesn't yet make it to the king.
					waitFor("go ahead with first");
				}
				stream.write((byte) 1);
			} else if (object instanceof SecondTransaction) {
				stream.write((byte) 2);
			} else if (object instanceof ThirdTransaction) {
				stream.write((byte) 3);
			} else {
				throw new RuntimeException("unknown: " + object.getClass());
			}
		}

		public Object readObject(InputStream stream) throws IOException, ClassNotFoundException {
			switch (stream.read()) {
				case 1: return new FirstTransaction();
				case 2: return new SecondTransaction();
				case 3: return new ThirdTransaction();
				default: throw new RuntimeException("unknown");
			}
		}

	}

}
