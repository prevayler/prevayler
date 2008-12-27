package org.prevayler.foundation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

public class DurableOutputStreamTest extends FileIOTest {

	public void testSingleThreaded() throws Exception {
		for (int i = 0; i < 10 /*5000*/; i++) {
//            System.out.println("i=" + i);

			File file = new File(_testDirectory, "stream" + i + ".bin");

			DurableOutputStream out = new DurableOutputStream(file);

			Turn myTurn = Turn.first();
			out.sync(new DummyGuide("first", myTurn));
			out.sync(new DummyGuide("second", myTurn.next()));
			out.close();

			assertTrue(out.reallyClosed());
			assertEquals(2, out.fileSyncCount());

			BufferedReader reader = new BufferedReader(new FileReader(file));
			assertEquals("first", reader.readLine());
			assertEquals("second", reader.readLine());
			assertEquals(null, reader.readLine());
			reader.close();

			delete(file);
		}
	}

	public void testMultiThreaded() throws Exception {
		for (int i = 0; i < 10 /*5000*/; i++) {
//            System.out.println("i=" + i);
			File file = new File(_testDirectory, "stream" + i + ".bin");
			DurableOutputStream out = new DurableOutputStream(file);

			Turn one = Turn.first();
			Turn two = one.next();
			Turn three = two.next();
			Turn four = three.next();
			Worker worker1 = new Worker(out, 1, two, four);
			Worker worker2 = new Worker(out, 2, one, three);

			Thread thread1 = new Thread(worker1, "Worker 1");
			Thread thread2 = new Thread(worker2, "Worker 2");

			assertEquals(0, out.fileSyncCount());
			thread1.start();
			assertEquals(0, out.fileSyncCount());

			thread2.start();
			thread1.join();
			thread2.join();

			int syncsBeforeClose = out.fileSyncCount();
			assertTrue(syncsBeforeClose >= 2);
			assertTrue(syncsBeforeClose <= 4);
			assertFalse(out.reallyClosed());

			out.close();

			assertTrue(out.reallyClosed());
			assertEquals(syncsBeforeClose, out.fileSyncCount());

			BufferedReader reader = new BufferedReader(new FileReader(file));
			assertEquals("2.first", reader.readLine());
			assertEquals("1.first", reader.readLine());
			assertEquals("2.second", reader.readLine());
			assertEquals("1.second", reader.readLine());
			assertEquals(null, reader.readLine());
			reader.close();

			delete(file);
		}
	}

	private static class DummyGuide extends Guided {

		private final String _value;

		public DummyGuide(String value, Turn turn) {
			super(turn);
			_value = value;
		}

		public void writeTo(OutputStream stream) throws IOException {
			stream.write(_value.getBytes());
			stream.write('\n');
		}

	}

	private static class Worker implements Runnable {

		private final DurableOutputStream _out;
		private final int _id;
		private final Turn _firstTurn;
		private final Turn _secondTurn;
		public Exception _ex;

		public Worker(DurableOutputStream out, int id, Turn firstTurn, Turn secondTurn) {
			_out = out;
			_id = id;
			_firstTurn = firstTurn;
			_secondTurn = secondTurn;
		}

		public void run() {
			try {
				_out.sync(new DummyGuide(_id + ".first", _firstTurn));
				_out.sync(new DummyGuide(_id + ".second", _secondTurn));
			} catch (IOException e) {
				_ex = e;
			}
		}

	}

}
