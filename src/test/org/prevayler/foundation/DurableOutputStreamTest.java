package org.prevayler.foundation;

import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.DurableOutputStream;
import org.prevayler.foundation.Turn;
import org.prevayler.foundation.serialization.JavaSerializationStrategy;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.EOFException;
import java.io.IOException;

public class DurableOutputStreamTest extends FileIOTest {

	public void testSingleThreaded() throws Exception {
		for (int i = 0; i < 10 /*5000*/; i++) {
//            System.out.println("i=" + i);

			File file = new File(_testDirectory, "stream" + i + ".bin");

			DurableOutputStream out = new DurableOutputStream(file, new JavaSerializationStrategy(null));

			Turn myTurn = Turn.first();
			out.sync("first", myTurn);
			out.sync("second", myTurn.next());
			out.close();

			assertTrue(out.reallyClosed());
			assertEquals(2, out.fileSyncCount());

			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			assertEquals("first", in.readObject());
			assertEquals("second", in.readObject());
			try {
				in.readObject();
				fail("expected end of file");
			} catch (EOFException e) {
			}

			in.close();

			delete(file);
		}
	}

	public void testMultiThreaded() throws Exception {
		for (int i = 0; i < 10 /*5000*/; i++) {
//            System.out.println("i=" + i);
			File file = new File(_testDirectory, "stream" + i + ".bin");
			DurableOutputStream out = new DurableOutputStream(file, new JavaSerializationStrategy(null));

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

			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			assertEquals("2.first", in.readObject());
			assertEquals("1.first", in.readObject());
			assertEquals("2.second", in.readObject());
			assertEquals("1.second", in.readObject());
			try {
				in.readObject();
				fail("expected end of file");
			} catch (EOFException e) {
			}

			in.close();

			delete(file);
		}
	}

	class Worker implements Runnable {
		DurableOutputStream _out;
		int _id;
		Turn _firstTurn;
		Turn _secondTurn;
		Exception _ex;

		Worker(DurableOutputStream out, int id, Turn firstTurn, Turn secondTurn) {
			_out = out;
			_id = id;
			_firstTurn = firstTurn;
			_secondTurn = secondTurn;
		}

		public void run() {
			try {
				_out.sync(_id + ".first", _firstTurn);
				_out.sync(_id + ".second", _secondTurn);
			} catch (IOException e) {
				_ex = e;
			}
		}
	}
}
