package org.prevayler.foundation;

import org.prevayler.foundation.monitor.NullMonitor;
import org.prevayler.foundation.serialization.JavaSerializer;
import org.prevayler.implementation.AppendTransaction;
import org.prevayler.implementation.TransactionGuide;
import org.prevayler.implementation.TransactionTimestamp;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class DurableOutputStreamTest extends FileIOTest {

	public void testSingleThreaded() throws Exception {
		for (int i = 0; i < 10 /*5000*/; i++) {
//            System.out.println("i=" + i);

			File file = new File(_testDirectory, "stream" + i + ".bin");

			DurableOutputStream out = new DurableOutputStream(file, new JavaSerializer());

			Turn myTurn = Turn.first();
			out.sync(new TransactionGuide(timestamp("first"), myTurn));
			out.sync(new TransactionGuide(timestamp("second"), myTurn.next()));
			out.close();

			assertTrue(out.reallyClosed());
			assertEquals(2, out.fileSyncCount());

			DurableInputStream in = new DurableInputStream(file, new JavaSerializer(), new NullMonitor());
			assertEquals("first", value(in.read()));
			assertEquals("second", value(in.read()));
			try {
				in.read();
				fail("expected end of file");
			} catch (EOFException e) {
			}

			in.close();

			delete(file);
		}
	}

	private long _systemVersion = 42;

	private TransactionTimestamp timestamp(String value) {
		return new TransactionTimestamp(new AppendTransaction(value), _systemVersion++, new Date());
	}

	private String value(TransactionTimestamp timestamp) {
		return ((AppendTransaction) timestamp.transaction()).toAdd;
	}

	public void testMultiThreaded() throws Exception {
		for (int i = 0; i < 10 /*5000*/; i++) {
//            System.out.println("i=" + i);
			File file = new File(_testDirectory, "stream" + i + ".bin");
			DurableOutputStream out = new DurableOutputStream(file, new JavaSerializer());

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

			DurableInputStream in = new DurableInputStream(file, new JavaSerializer(), new NullMonitor());
			assertEquals("2.first", value(in.read()));
			assertEquals("1.first", value(in.read()));
			assertEquals("2.second", value(in.read()));
			assertEquals("1.second", value(in.read()));
			try {
				in.read();
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
				_out.sync(new TransactionGuide(timestamp(_id + ".first"), _firstTurn));
				_out.sync(new TransactionGuide(timestamp(_id + ".second"), _secondTurn));
			} catch (IOException e) {
				_ex = e;
			}
		}
	}
}
