//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Transaction;
import org.prevayler.foundation.FileIOTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Date;

public class ConfusedFoodTasterStressTest extends FileIOTest {

	// Try increasing these numbers to test with more stress:
	private static final int NUMBER_OF_THREADS = 10; // affects how many concurrent transactions are attempted
	private static final int TRANSACTIONS_PER_THREAD = 100; // just affects how long the test runs before declaring success
	private static final int WHEN_TO_START_THROWING = 5; // affects how many transactions have a chance to get into the pipeline

	private Prevayler _prevayler;
	private volatile boolean _failed;

	public void testFoodTasting() throws Exception {
		_prevayler = PrevaylerFactory.createPrevayler(new CountingSystem(), _testDirectory);

		_failed = false;

		Thread[] threads = new Thread[NUMBER_OF_THREADS];
		for (int i = 0; i < NUMBER_OF_THREADS; i++) {
			threads[i] = new CountThread();
		}

		for (int i = 0; i < NUMBER_OF_THREADS; i++) {
			threads[i].start();
		}

		for (int i = 0; i < NUMBER_OF_THREADS; i++) {
			threads[i].join();
		}

		_prevayler.close();

		assertFalse(_failed);
	}

	public class CountThread extends Thread {
		public void run() {
			for (int i = 0; !_failed && i < TRANSACTIONS_PER_THREAD; i++) {
				try {
					_prevayler.execute(new CountTransaction());
				} catch (CountException exception) {
					String stackTrace = stackTrace(exception);
					if (stackTrace.indexOf("org.prevayler.implementation.PrevaylerImpl$1.receive(") != -1) {
						// Should not have gotten to the king!
						_failed = true;
						return;
					} else if (stackTrace.indexOf("org.prevayler.implementation.publishing.censorship.StrictTransactionCensor.approve(") != -1) {
						// Still in the food taster; okay.
						continue;
					} else {
						// Something unexpected.
						_failed = true;
						exception.printStackTrace();
						return;
					}
				} catch (RuntimeException exception) {
					// Something unexpected.
					_failed = true;
					exception.printStackTrace();
					return;
				}
			}
		}

		private String stackTrace(CountException exception) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			exception.printStackTrace(new PrintStream(stream));
			return stream.toString();
		}
	}

	public static class CountingSystem implements Serializable {
		private static final long serialVersionUID = -361279281565153469L;
		int counter = 0;
	}

	public static class CountTransaction implements Transaction {
		private static final long serialVersionUID = 5043457505878510633L;

		public void executeOn(Object prevalentSystem, Date executionTime) {
			CountingSystem countingSystem = (CountingSystem) prevalentSystem;
			if (countingSystem.counter == WHEN_TO_START_THROWING) {
				throw new CountException();
			}
			countingSystem.counter++;
		}
	}

	public static class CountException extends RuntimeException {
		private static final long serialVersionUID = -5965497237902430070L;
	}

}
