package org.prevayler.implementation.snapshot;

import org.prevayler.foundation.FileIOTest;

import java.io.IOException;

public class SkaringaSnapshotManagerTest extends FileIOTest {
	public void testNoExistingSnapshot() throws IOException, ClassNotFoundException {
		SkaringaSnapshotManager manager = new SkaringaSnapshotManager("initial", _testDirectory);
		assertEquals("initial", manager.recoveredPrevalentSystem());
	}

	public void testReadExistingSnapshot() throws IOException, ClassNotFoundException {
		SkaringaSnapshotManager original = new SkaringaSnapshotManager("initial", _testDirectory);
		original.writeSnapshot("snapshotted", 123);

		SkaringaSnapshotManager newManager = new SkaringaSnapshotManager("initial", _testDirectory);
		assertEquals("snapshotted", newManager.recoveredPrevalentSystem());
	}
}
