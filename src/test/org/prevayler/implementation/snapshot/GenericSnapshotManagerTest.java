package org.prevayler.implementation.snapshot;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.serialization.JavaSerializationStrategy;
import org.prevayler.foundation.serialization.XStreamSerializationStrategy;
import org.prevayler.foundation.serialization.SerializationStrategy;
import org.prevayler.implementation.AppendTransaction;

import java.io.File;
import java.io.IOException;

public class GenericSnapshotManagerTest extends FileIOTest {

	public void testNoExistingSnapshot() throws IOException, ClassNotFoundException {
		Prevayler prevayler = createPrevayler("snapshot", new JavaSerializationStrategy());
		assertEquals("initial", prevayler.prevalentSystem().toString());
	}

	public void testReadExistingSnapshot() throws IOException, ClassNotFoundException {
		Prevayler first = createPrevayler("snapshot", new JavaSerializationStrategy());
		appendTakeSnapshotAndClose(first);

		checkSnapshotAndDeleteJournal("0000000000000000002.snapshot", "0000000000000000001.journal");

		Prevayler second = createPrevayler("snapshot", new JavaSerializationStrategy());
		assertEquals("initial one two", second.prevalentSystem().toString());
		second.close();
	}

	public void testDetectExistingSnapshotFromDifferentSnapshotManager() throws IOException, ClassNotFoundException {
		Prevayler first = createPrevayler("xstreamsnapshot", new XStreamSerializationStrategy());
		appendTakeSnapshotAndClose(first);

		try {
			createPrevayler("snapshot", new JavaSerializationStrategy());
			fail();
		} catch (IOException e) {
			// This is good because if we only looked for .snapshot files we could silently ignore an existing snapshot.
			assertTrue("Actual message was <" + e.getMessage() + ">", e.getMessage().endsWith("0000000000000000002.xstreamsnapshot cannot be read; only [snapshot] supported"));
		}
	}

	public void testMultipleSerializationStrategiesFromXStream() throws IOException, ClassNotFoundException {
		Prevayler prevayler = createPrevayler("xstreamsnapshot", new XStreamSerializationStrategy());
		appendTakeSnapshotAndClose(prevayler);

		checkSnapshotAndDeleteJournal("0000000000000000002.xstreamsnapshot", "0000000000000000001.journal");

		checkCanReadSnapshotWithMultipleStrategies();
	}

	public void testMultipleSerializationStrategiesFromJava() throws IOException, ClassNotFoundException {
		Prevayler prevayler = createPrevayler("snapshot", new JavaSerializationStrategy());
		appendTakeSnapshotAndClose(prevayler);

		checkSnapshotAndDeleteJournal("0000000000000000002.snapshot", "0000000000000000001.journal");

		checkCanReadSnapshotWithMultipleStrategies();
	}

	public void testUsePrimaryForWritingSnapshot() throws IOException, ClassNotFoundException {
		Prevayler first = createPrevaylerMulti();
		appendTakeSnapshotAndClose(first);
		checkSnapshotAndDeleteJournal("0000000000000000002.xstreamsnapshot", "0000000000000000001.journal");

		Prevayler second = createPrevayler("xstreamsnapshot", new XStreamSerializationStrategy());
		assertEquals("initial one two", second.prevalentSystem().toString());
		second.close();
	}

	private void checkCanReadSnapshotWithMultipleStrategies() throws IOException, ClassNotFoundException {
		Prevayler prevayler = createPrevaylerMulti();
		assertEquals("initial one two", prevayler.prevalentSystem().toString());
		prevayler.close();
	}

	private Prevayler createPrevayler(String suffix, SerializationStrategy strategy) throws IOException, ClassNotFoundException {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(new StringBuffer("initial"));
		factory.configurePrevalenceDirectory(_testDirectory);
		factory.configureSnapshotSerializationStrategy(suffix, strategy);
		return factory.create();
	}

	private void appendTakeSnapshotAndClose(Prevayler prevayler) throws IOException {
		prevayler.execute(new AppendTransaction(" one"));
		prevayler.execute(new AppendTransaction(" two"));
		prevayler.takeSnapshot();
		prevayler.close();
	}

	private void checkSnapshotAndDeleteJournal(String snapshot, String journal) {
		assertTrue(new File(_testDirectory, snapshot).exists());
		deleteFromTestDirectory(journal);
	}

	private Prevayler createPrevaylerMulti() throws IOException, ClassNotFoundException {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(new StringBuffer("initial"));
		factory.configurePrevalenceDirectory(_testDirectory);
		factory.configureSnapshotSerializationStrategy("xstreamsnapshot", new XStreamSerializationStrategy());
		factory.configureSnapshotSerializationStrategy("snapshot", new JavaSerializationStrategy());
		return factory.create();
	}

}
