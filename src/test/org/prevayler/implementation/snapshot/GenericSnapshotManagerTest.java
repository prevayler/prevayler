// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

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

    public void testNoExistingSnapshot() throws IOException {
        Prevayler<StringBuilder> prevayler = createPrevayler("snapshot", new JavaSerializer<StringBuilder>());
        assertEquals("initial", prevayler.prevalentSystem().toString());
    }

    public void testRoundtripJava() throws IOException {
        checkRoundtrip("snapshot", new JavaSerializer<StringBuilder>());
    }

    public void testRoundtripXStream() throws IOException {
        checkRoundtrip("xstreamsnapshot", new XStreamSerializer<StringBuilder>());
    }

    public void testRoundtripSkaringa() throws IOException {
        checkRoundtrip("skaringasnapshot", new SkaringaSerializer<StringBuilder>());
    }

    private void checkRoundtrip(String suffix, Serializer<StringBuilder> serializer) throws IOException {
        Prevayler<StringBuilder> first = createPrevayler(suffix, serializer);
        appendTakeSnapshotAndClose(first);

        checkSnapshotAndDeleteJournal("0000000000000000002." + suffix, "0000000000000000001.journal");

        Prevayler<StringBuilder> second = createPrevayler(suffix, serializer);
        assertEquals("initial one two", second.prevalentSystem().toString());
        second.close();
    }

    public void testDetectExistingSnapshotFromUnknownSnapshotManager() throws IOException {
        Prevayler<StringBuilder> first = createPrevayler("xstreamsnapshot", new XStreamSerializer<StringBuilder>());
        appendTakeSnapshotAndClose(first);

        try {
            createPrevayler("snapshot", new JavaSerializer<StringBuilder>());
            fail();
        } catch (SnapshotError e) {
            // This is good because if we only looked for .snapshot files we
            // could silently ignore an existing snapshot.
            assertTrue(e.getMessage().endsWith("0000000000000000002.xstreamsnapshot cannot be read; only [snapshot] supported"));
        }
    }

    public void testMultipleSerializationStrategiesFromXStream() throws IOException {
        Prevayler<StringBuilder> prevayler = createPrevayler("xstreamsnapshot", new XStreamSerializer<StringBuilder>());
        appendTakeSnapshotAndClose(prevayler);

        checkSnapshotAndDeleteJournal("0000000000000000002.xstreamsnapshot", "0000000000000000001.journal");

        checkCanReadSnapshotWithMultipleStrategies();
    }

    public void testMultipleSerializationStrategiesFromJava() throws IOException {
        Prevayler<StringBuilder> prevayler = createPrevayler("snapshot", new JavaSerializer<StringBuilder>());
        appendTakeSnapshotAndClose(prevayler);

        checkSnapshotAndDeleteJournal("0000000000000000002.snapshot", "0000000000000000001.journal");

        checkCanReadSnapshotWithMultipleStrategies();
    }

    public void testUsePrimaryForWritingSnapshot() throws IOException {
        Prevayler<StringBuilder> first = createPrevaylerMulti();
        appendTakeSnapshotAndClose(first);
        checkSnapshotAndDeleteJournal("0000000000000000002.xstreamsnapshot", "0000000000000000001.journal");

        Prevayler<StringBuilder> second = createPrevayler("xstreamsnapshot", new XStreamSerializer<StringBuilder>());
        assertEquals("initial one two", second.prevalentSystem().toString());
        second.close();
    }

    private void checkCanReadSnapshotWithMultipleStrategies() throws IOException {
        Prevayler<StringBuilder> prevayler = createPrevaylerMulti();
        assertEquals("initial one two", prevayler.prevalentSystem().toString());
        prevayler.close();
    }

    private Prevayler<StringBuilder> createPrevaylerMulti() throws IOException {
        PrevaylerFactory<StringBuilder> factory = new PrevaylerFactory<StringBuilder>();
        factory.configurePrevalentSystem(new StringBuilder("initial"));
        factory.configurePrevalenceDirectory(_testDirectory);
        factory.configureSnapshotSerializer("xstreamsnapshot", new XStreamSerializer<StringBuilder>());
        factory.configureSnapshotSerializer("snapshot", new JavaSerializer<StringBuilder>());
        return factory.create();
    }

    private Prevayler<StringBuilder> createPrevayler(String suffix, Serializer<StringBuilder> serializer) throws IOException {
        PrevaylerFactory<StringBuilder> factory = new PrevaylerFactory<StringBuilder>();
        factory.configurePrevalentSystem(new StringBuilder("initial"));
        factory.configurePrevalenceDirectory(_testDirectory);
        factory.configureSnapshotSerializer(suffix, serializer);
        return factory.create();
    }

    private void appendTakeSnapshotAndClose(Prevayler<StringBuilder> prevayler) throws IOException {
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
