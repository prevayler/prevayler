package org.prevayler.foundation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileLockerTest extends FileIOTest {

	private FileLocker _sharedLocker;
	private boolean _done;
	private Exception _exception;

	private synchronized void shareLocker(FileLocker locker) {
		_sharedLocker = locker;
		notifyAll();
	}

	private synchronized FileLocker waitForLocker() throws InterruptedException {
		while (_sharedLocker == null) {
			wait();
		}
		return _sharedLocker;
	}

	private synchronized void setDone() {
		_done = true;
		notifyAll();
	}

	private synchronized void waitForDone() throws InterruptedException {
		while (!_done) {
			wait();
		}
	}

	private synchronized void stashException(Exception e) {
		_exception = e;
	}

	public void testFileLock() throws Exception {
		final File lockFile = new File(_testDirectory, "test.lock");

		// We can acquire a new lock...
		final FileLocker locker1 = new FileLocker(lockFile);

		// We can't acquire again in the same JVM...
		try {
			new FileLocker(lockFile);
			fail();
		} catch (IOException e) {
			assertEquals("Already locked internally", e.getMessage());
		}

		// We can't acquire in another JVM...
		runProcess(lockFile, "Failed! Already locked externally");

		// We can acquire again in the same JVM after releasing...
		locker1.release();
		final FileLocker locker2 = new FileLocker(lockFile);

		// We can acquire in another JVM after releasing...
		locker2.release();
		runProcess(lockFile, "Locked!");

		// We can acquire in this JVM after other JVM exited without explicitly releasing...
		final FileLocker locker3 = new FileLocker(lockFile);
		locker3.release();

		// We can acquire and release in different threads in this JVM and then acquire in another JVM...
		Thread acquire = new Thread() {
			public void run() {
				try {
					shareLocker(new FileLocker(lockFile));
					waitForDone();
				} catch (Exception e) {
					stashException(e);
				}
			}
		};
		Thread release = new Thread() {
			public void run() {
				try {
					FileLocker locker = waitForLocker();
					locker.release();
				} catch (Exception e) {
					stashException(e);
				}
			}
		};
		acquire.start();
		release.start();
		release.join();
		runProcess(lockFile, "Locked!");
		setDone();
		acquire.join();
		synchronized (this) {
			if (_exception != null) {
				fail(_exception.getMessage());
			}
		}
	}

	private void runProcess(File lockFile, String expectedOutput) throws IOException, InterruptedException {
		String[] command = {"java", "-classpath", System.getProperty("java.class.path"),
							LockingMain.class.getName(), lockFile.getCanonicalPath()};
		Process process = Runtime.getRuntime().exec(command);
		BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
		assertEquals(expectedOutput, output.readLine());
		process.waitFor();
	}

	public static class LockingMain {
		public static void main(String[] args) {
			File lockFile = new File(args[0]);
			try {
				new FileLocker(lockFile);
				System.out.println("Locked!");
				// But don't release, to prove that the lock is released when the process exits.
			} catch (IOException e) {
				System.out.println("Failed! " + e.getMessage());
			}
		}
	}

}
