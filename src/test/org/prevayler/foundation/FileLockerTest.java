package org.prevayler.foundation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileLockerTest extends FileIOTest {

	public void testFileLock() throws Exception {
		File lockFile = new File(_testDirectory, "test.lock");

		// We can acquire a new lock...
		assertNotNull(FileLocker.acquire(lockFile));

		// We can't acquire again in the same JVM...
		assertNull(FileLocker.acquire(lockFile));

		// We can't acquire in another JVM...
		runProcess(lockFile, "Failed!");

		// We can acquire again in the same JVM after releasing...
		FileLocker.release(lockFile);
		assertNotNull(FileLocker.acquire(lockFile));

		// We can acquire in another JVM after releasing...
		FileLocker.release(lockFile);
		runProcess(lockFile, "Locked!");

		// We can acquire in this JVM after other JVM exited without explicitly releasing...
		assertNotNull(FileLocker.acquire(lockFile));

		// Just be nice to tearDown and other tests...
		FileLocker.release(lockFile);
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
		public static void main(String[] args) throws IOException {
			File lockFile = new File(args[0]);
			if (FileLocker.acquire(lockFile) != null) {
				System.out.println("Locked!");
				// But don't release, to prove that the lock is released when the process exits.
			} else {
				System.out.println("Failed!");
			}
		}
	}

}
