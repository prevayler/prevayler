package org.prevayler.implementation.snapshot;

import org.prevayler.foundation.FileIOTest;

import java.io.IOException;

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
}
