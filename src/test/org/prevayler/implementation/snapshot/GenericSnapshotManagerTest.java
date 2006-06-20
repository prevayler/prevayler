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
        Prevayler<StringBuffer> prevayler = createPrevayler("snapshot", new JavaSerializer());
        assertEquals("initial", prevayler.prevalentSystem().toString());
    }

    public void testRoundtripJava() throws IOException {
        checkRoundtrip("snapshot", new JavaSerializer());
    }

    public void testRoundtripXStream() throws IOException {
        checkRoundtrip("xstreamsnapshot", new XStreamSerializer());
    }

    public void testRoundtripSkaringa() throws IOException {
        checkRoundtrip("skaringasnapshot", new SkaringaSerializer());
    }

    private void checkRoundtrip(String suffix, Serializer serializer) throws IOException {
        Prevayler<StringBuffer> first = createPrevayler(suffix, serializer);
        appendTakeSnapshotAndClose(first);

        checkSnapshotAndDeleteJournal("0000000000000000002." + suffix, "0000000000000000001.journal");

        Prevayler<StringBuffer> second = createPrevayler(suffix, serializer);
        assertEquals("initial one two", second.prevalentSystem().toString());
        second.close();
    }

    public void testDetectExistingSnapshotFromUnknownSnapshotManager() throws IOException {
        Prevayler<StringBuffer> first = createPrevayler("xstreamsnapshot", new XStreamSerializer());
        appendTakeSnapshotAndClose(first);

        try {
            createPrevayler("snapshot", new JavaSerializer());
            fail();
        } catch (SnapshotError e) {
            // This is good because if we only looked for .snapshot files we
            // could silently ignore an existing snapshot.
            assertTrue(e.getMessage().endsWith("0000000000000000002.xstreamsnapshot cannot be read; only [snapshot] supported"));
        }
    }

    public void testMultipleSerializationStrategiesFromXStream() throws IOException {
        Prevayler<StringBuffer> prevayler = createPrevayler("xstreamsnapshot", new XStreamSerializer());
        appendTakeSnapshotAndClose(prevayler);

        checkSnapshotAndDeleteJournal("0000000000000000002.xstreamsnapshot", "0000000000000000001.journal");

        checkCanReadSnapshotWithMultipleStrategies();
    }

    public void testMultipleSerializationStrategiesFromJava() throws IOException {
        Prevayler<StringBuffer> prevayler = createPrevayler("snapshot", new JavaSerializer());
        appendTakeSnapshotAndClose(prevayler);

        checkSnapshotAndDeleteJournal("0000000000000000002.snapshot", "0000000000000000001.journal");

        checkCanReadSnapshotWithMultipleStrategies();
    }

    public void testUsePrimaryForWritingSnapshot() throws IOException {
        Prevayler<StringBuffer> first = createPrevaylerMulti();
        appendTakeSnapshotAndClose(first);
        checkSnapshotAndDeleteJournal("0000000000000000002.xstreamsnapshot", "0000000000000000001.journal");

        Prevayler<StringBuffer> second = createPrevayler("xstreamsnapshot", new XStreamSerializer());
        assertEquals("initial one two", second.prevalentSystem().toString());
        second.close();
    }

    private void checkCanReadSnapshotWithMultipleStrategies() throws IOException {
        Prevayler<StringBuffer> prevayler = createPrevaylerMulti();
        assertEquals("initial one two", prevayler.prevalentSystem().toString());
        prevayler.close();
    }

    private Prevayler<StringBuffer> createPrevaylerMulti() throws IOException {
        PrevaylerFactory<StringBuffer> factory = new PrevaylerFactory<StringBuffer>();
        factory.configurePrevalentSystem(new StringBuffer("initial"));
        factory.configurePrevalenceDirectory(_testDirectory);
        factory.configureSnapshotSerializer("xstreamsnapshot", new XStreamSerializer());
        factory.configureSnapshotSerializer("snapshot", new JavaSerializer());
        return factory.create();
    }

    private Prevayler<StringBuffer> createPrevayler(String suffix, Serializer serializer) throws IOException {
        PrevaylerFactory<StringBuffer> factory = new PrevaylerFactory<StringBuffer>();
        factory.configurePrevalentSystem(new StringBuffer("initial"));
        factory.configurePrevalenceDirectory(_testDirectory);
        factory.configureSnapshotSerializer(suffix, serializer);
        return factory.create();
    }

    private void appendTakeSnapshotAndClose(Prevayler<StringBuffer> prevayler) throws IOException {
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
