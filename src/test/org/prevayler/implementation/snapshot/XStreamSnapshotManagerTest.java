package org.prevayler.implementation.snapshot;

import org.prevayler.foundation.FileIOTest;

import java.io.IOException;

public class XStreamSnapshotManagerTest extends FileIOTest {
	public void testNoExistingSnapshot() throws IOException, ClassNotFoundException {
		XStreamSnapshotManager manager = new XStreamSnapshotManager("initial", _testDirectory);
		assertEquals("initial", manager.recoveredPrevalentSystem());
	}

	public void testReadExistingSnapshot() throws IOException, ClassNotFoundException {
		XStreamSnapshotManager original = new XStreamSnapshotManager("initial", _testDirectory);
		original.writeSnapshot("snapshotted", 123);

		XStreamSnapshotManager newManager = new XStreamSnapshotManager("initial", _testDirectory);
		assertEquals("snapshotted", newManager.recoveredPrevalentSystem());
	}
}
