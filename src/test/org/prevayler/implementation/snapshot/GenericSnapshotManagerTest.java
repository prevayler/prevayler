package org.prevayler.implementation.snapshot;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.serialization.JavaSerializer;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.foundation.serialization.SkaringaSerializer;
import org.prevayler.foundation.serialization.XStreamSerializer;
import org.prevayler.implementation.AppendTransaction;

import java.io.File;
import java.io.IOException;

public class GenericSnapshotManagerTest extends FileIOTest {

	public void testNoExistingSnapshot() throws IOException, ClassNotFoundException {
		Prevayler prevayler = createPrevayler("snapshot", new JavaSerializer());
		assertEquals("initial", prevayler.prevalentSystem().toString());
	}

	public void testRoundtripJava() throws IOException, ClassNotFoundException {
		checkRoundtrip("snapshot", new JavaSerializer());
	}

	public void testRoundtripXStream() throws IOException, ClassNotFoundException {
		checkRoundtrip("xstreamsnapshot", new XStreamSerializer());
	}

	public void testRoundtripSkaringa() throws IOException, ClassNotFoundException {
		checkRoundtrip("skaringasnapshot", new SkaringaSerializer());
	}

	private void checkRoundtrip(String suffix, Serializer serializer) throws IOException, ClassNotFoundException {
		Prevayler first = createPrevayler(suffix, serializer);
		appendTakeSnapshotAndClose(first);

		checkSnapshotAndDeleteJournal("0000000000000000002." + suffix, "0000000000000000001.journal");

		Prevayler second = createPrevayler(suffix, serializer);
		assertEquals("initial one two", second.prevalentSystem().toString());
		second.close();
	}

	public void testDetectExistingSnapshotFromUnknownSnapshotManager() throws IOException, ClassNotFoundException {
		Prevayler first = createPrevayler("xstreamsnapshot", new XStreamSerializer());
		appendTakeSnapshotAndClose(first);

		try {
			createPrevayler("snapshot", new JavaSerializer());
			fail();
		} catch (IOException e) {
			// This is good because if we only looked for .snapshot files we could silently ignore an existing snapshot.
			assertTrue("Actual message was <" + e.getMessage() + ">",
					e.getMessage().endsWith("0000000000000000002.xstreamsnapshot cannot be read; only [snapshot] supported"));
		}
	}

	public void testMultipleSerializationStrategiesFromXStream() throws IOException, ClassNotFoundException {
		Prevayler prevayler = createPrevayler("xstreamsnapshot", new XStreamSerializer());
		appendTakeSnapshotAndClose(prevayler);

		checkSnapshotAndDeleteJournal("0000000000000000002.xstreamsnapshot", "0000000000000000001.journal");

		checkCanReadSnapshotWithMultipleStrategies();
	}

	public void testMultipleSerializationStrategiesFromJava() throws IOException, ClassNotFoundException {
		Prevayler prevayler = createPrevayler("snapshot", new JavaSerializer());
		appendTakeSnapshotAndClose(prevayler);

		checkSnapshotAndDeleteJournal("0000000000000000002.snapshot", "0000000000000000001.journal");

		checkCanReadSnapshotWithMultipleStrategies();
	}

	public void testUsePrimaryForWritingSnapshot() throws IOException, ClassNotFoundException {
		Prevayler first = createPrevaylerMulti();
		appendTakeSnapshotAndClose(first);
		checkSnapshotAndDeleteJournal("0000000000000000002.xstreamsnapshot", "0000000000000000001.journal");

		Prevayler second = createPrevayler("xstreamsnapshot", new XStreamSerializer());
		assertEquals("initial one two", second.prevalentSystem().toString());
		second.close();
	}

	private void checkCanReadSnapshotWithMultipleStrategies() throws IOException, ClassNotFoundException {
		Prevayler prevayler = createPrevaylerMulti();
		assertEquals("initial one two", prevayler.prevalentSystem().toString());
		prevayler.close();
	}

	private Prevayler createPrevaylerMulti() throws IOException, ClassNotFoundException {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(new StringBuffer("initial"));
		factory.configurePrevalenceDirectory(_testDirectory);
		factory.configureSnapshotSerializer("xstreamsnapshot", new XStreamSerializer());
		factory.configureSnapshotSerializer("snapshot", new JavaSerializer());
		return factory.create();
	}

	private Prevayler createPrevayler(String suffix, Serializer serializer) throws IOException,
			ClassNotFoundException {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(new StringBuffer("initial"));
		factory.configurePrevalenceDirectory(_testDirectory);
		factory.configureSnapshotSerializer(suffix, serializer);
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

}
