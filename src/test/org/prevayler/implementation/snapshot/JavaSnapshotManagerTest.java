package org.prevayler.implementation.snapshot;

import org.prevayler.foundation.FileIOTest;

import java.io.IOException;
import java.io.FileNotFoundException;

public class JavaSnapshotManagerTest extends FileIOTest {
	public void testNoExistingSnapshot() throws IOException, ClassNotFoundException {
		JavaSnapshotManager manager = new JavaSnapshotManager("initial", _testDirectory, null);
		assertEquals("initial", manager.recoveredPrevalentSystem());
	}

	public void testReadExistingSnapshot() throws IOException, ClassNotFoundException {
		JavaSnapshotManager original = new JavaSnapshotManager("initial", _testDirectory, null);
		original.writeSnapshot("snapshotted", 123);

		JavaSnapshotManager newManager = new JavaSnapshotManager("initial", _testDirectory, null);
		assertEquals("snapshotted", newManager.recoveredPrevalentSystem());
	}

	public void testDetectExistingSnapshotFromDifferentSnapshotManager() throws IOException, ClassNotFoundException {
		XStreamSnapshotManager original = new XStreamSnapshotManager("initial", _testDirectory);
		original.writeSnapshot("snapshotted", 123);

		try {
			new JavaSnapshotManager("initial", _testDirectory, null);
			fail();
		} catch (IOException e) {
			// This is good because if we only looked for .snapshot files we could silently ignore an existing snapshot.
			assertTrue(e.getMessage(), e.getMessage().endsWith("0000000000000000123.xstreamsnapshot cannot be read by org.prevayler.implementation.snapshot.JavaSnapshotManager"));
		}
	}
}
