package org.prevayler.implementation.snapshot;

import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.serialization.JavaSerializationStrategy;
import org.prevayler.foundation.serialization.XStreamSerializationStrategy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GenericSnapshotManagerTest extends FileIOTest {
	public void testNoExistingSnapshot() throws IOException, ClassNotFoundException {
		GenericSnapshotManager manager = new GenericSnapshotManager(new JavaSerializationStrategy(null), "initial", _testDirectory);
		assertEquals("initial", manager.recoveredPrevalentSystem());
	}

	public void testReadExistingSnapshot() throws IOException, ClassNotFoundException {
		GenericSnapshotManager original = new GenericSnapshotManager(new JavaSerializationStrategy(null), "initial", _testDirectory);
		original.writeSnapshot("snapshotted", 123);

		GenericSnapshotManager newManager = new GenericSnapshotManager(new JavaSerializationStrategy(null), "initial", _testDirectory);
		assertEquals("snapshotted", newManager.recoveredPrevalentSystem());
	}

	public void testDetectExistingSnapshotFromDifferentSnapshotManager() throws IOException, ClassNotFoundException {
		XStreamSnapshotManager original = new XStreamSnapshotManager("initial", _testDirectory);
		original.writeSnapshot("snapshotted", 123);

		try {
			new GenericSnapshotManager(new JavaSerializationStrategy(null), "initial", _testDirectory);
			fail();
		} catch (IOException e) {
			// This is good because if we only looked for .snapshot files we could silently ignore an existing snapshot.
			assertTrue(e.getMessage().endsWith("/0000000000000000123.xstreamsnapshot cannot be read by org.prevayler.implementation.snapshot.GenericSnapshotManager"));
		}
	}

	public void testMultipleSerializationStrategiesFromXStream() throws IOException, ClassNotFoundException {
		XStreamSnapshotManager original = new XStreamSnapshotManager("initial", _testDirectory);
		original.writeSnapshot("snapshotted", 123);
		assertTrue(new File(_testDirectory, "0000000000000000123.xstreamsnapshot").exists());

		checkCanReadSnapshotWithMultipleStrategies();
	}

	public void testMultipleSerializationStrategiesFromJava() throws IOException, ClassNotFoundException {
		JavaSnapshotManager original = new JavaSnapshotManager("initial", _testDirectory, null);
		original.writeSnapshot("snapshotted", 123);
		assertTrue(new File(_testDirectory, "0000000000000000123.snapshot").exists());

		checkCanReadSnapshotWithMultipleStrategies();
	}

	private void checkCanReadSnapshotWithMultipleStrategies() throws IOException, ClassNotFoundException {
		GenericSnapshotManager generic = makeWithMultipleStrategies();
		assertEquals("snapshotted", generic.recoveredPrevalentSystem());

		generic.writeSnapshot("snapshotted again", 124);
		assertTrue(new File(_testDirectory, "0000000000000000124.xstreamsnapshot").exists());
	}

	private GenericSnapshotManager makeWithMultipleStrategies() throws IOException, ClassNotFoundException {
		Map strategies = new HashMap();
		strategies.put("xstreamsnapshot", new XStreamSerializationStrategy());
		strategies.put("snapshot", new JavaSerializationStrategy(null));
		return new GenericSnapshotManager(strategies, "xstreamsnapshot", "initial", _testDirectory);
	}

	public void testWritingWithPrimarySerializationStrategy() throws IOException, ClassNotFoundException {
		GenericSnapshotManager generic = makeWithMultipleStrategies();

		generic.writeSnapshot("snapshotted", 123);
		assertTrue(new File(_testDirectory, "0000000000000000123.xstreamsnapshot").exists());

		XStreamSnapshotManager xstream = new XStreamSnapshotManager("initial", _testDirectory);
		assertEquals("snapshotted", xstream.recoveredPrevalentSystem());
	}
}
