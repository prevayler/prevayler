// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation;

import org.prevayler.Clock;
import org.prevayler.GenericTransaction;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.serialization.DESSerializer;
import org.prevayler.foundation.serialization.GZIPSerializer;
import org.prevayler.foundation.serialization.JavaSerializer;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.foundation.serialization.SkaringaSerializer;
import org.prevayler.foundation.serialization.XStreamSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

public class JournalSerializerTest extends FileIOTest {

    public void testConfigureJournalSerializationStrategy() throws IOException {
        Serializer<GenericTransaction> strategy = new MySerializer();

        startAndCrash("MyJournal", strategy);

        String expected = "6;systemVersion=1;executionTime=1000002\r\n";
        expected += " first\r\n";
        expected += "7;systemVersion=2;executionTime=1000004\r\n";
        expected += " second\r\n";
        expected += "6;systemVersion=3;executionTime=1000006\r\n";
        expected += " third\r\n";
        assertEquals(expected, journalContents("MyJournal"));

        recover("MyJournal", strategy);
    }

    public void testBadSuffix() {
        PrevaylerFactory<Void> factory = new PrevaylerFactory<Void>();
        try {
            factory.configureJournalSerializer("JOURNAL", new JavaSerializer<GenericTransaction>());
            fail();
        } catch (IllegalArgumentException expected) {
            assertEquals("Journal filename suffix must match /[a-zA-Z0-9]*[Jj]ournal/, but 'JOURNAL' does not", expected.getMessage());
        }
    }

    public void testTryToConfigureTwo() {
        PrevaylerFactory<Void> factory = new PrevaylerFactory<Void>();
        factory.configureJournalSerializer("journal", new JavaSerializer<GenericTransaction>());
        try {
            factory.configureJournalSerializer("newjournal", new JavaSerializer<GenericTransaction>());
            fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testJavaJournal() throws IOException {
        Serializer<GenericTransaction> strategy = new JavaSerializer<GenericTransaction>();

        startAndCrash("journal", strategy);
        recover("journal", strategy);
    }

    public void testXStreamJournal() throws IOException {
        Serializer<GenericTransaction> strategy = new XStreamSerializer<GenericTransaction>();

        startAndCrash("journal", strategy);
        recover("journal", strategy);
    }

    public void testSkaringaJournal() throws IOException {
        Serializer<GenericTransaction> strategy = new SkaringaSerializer<GenericTransaction>();

        startAndCrash("journal", strategy);
        recover("journal", strategy);
    }

    public void testCompressedAndEncryptedJournal() throws Exception {
        byte[] key = { 35, 24, 45, 123, 86, 36, 21, 1 };
        JavaSerializer<GenericTransaction> java = new JavaSerializer<GenericTransaction>();
        GZIPSerializer<GenericTransaction> gzip = new GZIPSerializer<GenericTransaction>(java);
        DESSerializer<GenericTransaction> des = new DESSerializer<GenericTransaction>(gzip, key);

        startAndCrash("journal", des);
        recover("journal", des);
    }

    public void testTripleDES() throws Exception {
        byte[] key = { 35, 24, 45, 123, 86, 36, 21, 1, 54, 45, 6, 123, 34, 57, 34, 75, 12, 32, 4, 7, 23, 78, 97, 4 };
        JavaSerializer<GenericTransaction> java = new JavaSerializer<GenericTransaction>();
        GZIPSerializer<GenericTransaction> gzip = new GZIPSerializer<GenericTransaction>(java);
        DESSerializer<GenericTransaction> des = new DESSerializer<GenericTransaction>(gzip, key);

        startAndCrash("journal", des);
        recover("journal", des);
    }

    private void startAndCrash(String suffix, Serializer<GenericTransaction> journalSerializer) throws IOException {
        Prevayler<StringBuilder> prevayler = createPrevayler(suffix, journalSerializer);

        prevayler.execute(new AppendTransaction(" first"));
        prevayler.execute(new AppendTransaction(" second"));
        prevayler.execute(new AppendTransaction(" third"));
        assertEquals("the system first second third", prevayler.execute(new ToStringQuery()));
        prevayler.close();
    }

    private void recover(String suffix, Serializer<GenericTransaction> journalSerializer) throws IOException {
        Prevayler<StringBuilder> prevayler = createPrevayler(suffix, journalSerializer);
        assertEquals("the system first second third", prevayler.execute(new ToStringQuery()));
    }

    private Prevayler<StringBuilder> createPrevayler(String suffix, Serializer<GenericTransaction> journalSerializer) throws IOException {
        PrevaylerFactory<StringBuilder> factory = new PrevaylerFactory<StringBuilder>();
        factory.configurePrevalentSystem(new StringBuilder("the system"));
        factory.configurePrevalenceDirectory(_testDirectory);
        factory.configureJournalSerializer(suffix, journalSerializer);
        factory.configureClock(new Clock() {
            private long time = 1000000;

            public Date time() {
                return new Date(++time);
            }
        });
        return factory.create();
    }

    private static class MySerializer implements Serializer<GenericTransaction> {

        public void writeObject(OutputStream stream, GenericTransaction object) throws Exception {
            Writer writer = new OutputStreamWriter(stream, "UTF-8");
            AppendTransaction transaction = (AppendTransaction) object;
            writer.write(transaction.toAdd);
            writer.flush();
        }

        public GenericTransaction readObject(InputStream stream) throws Exception {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            return new AppendTransaction(reader.readLine());
        }

    }

}
