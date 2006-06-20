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
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.FileIOTest;
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
        Serializer<Object> strategy = new MySerializer();

        startAndCrash("MyJournal", strategy);

        assertEquals("6;withQuery=false;systemVersion=1;executionTime=1000002\r\n" + " first\r\n" + "7;withQuery=false;systemVersion=2;executionTime=1000004\r\n" + " second\r\n" + "6;withQuery=false;systemVersion=3;executionTime=1000006\r\n" + " third\r\n", journalContents("MyJournal"));

        recover("MyJournal", strategy);
    }

    public void testBadSuffix() {
        PrevaylerFactory<Void> factory = new PrevaylerFactory<Void>();
        try {
            factory.configureJournalSerializer("JOURNAL", new JavaSerializer<Object>());
            fail();
        } catch (IllegalArgumentException expected) {
            assertEquals("Journal filename suffix must match /[a-zA-Z0-9]*[Jj]ournal/, but 'JOURNAL' does not", expected.getMessage());
        }
    }

    public void testTryToConfigureTwo() {
        PrevaylerFactory<Void> factory = new PrevaylerFactory<Void>();
        factory.configureJournalSerializer("journal", new JavaSerializer<Object>());
        try {
            factory.configureJournalSerializer("newjournal", new JavaSerializer<Object>());
            fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testJavaJournal() throws IOException {
        Serializer<Object> strategy = new JavaSerializer<Object>();

        startAndCrash("journal", strategy);
        recover("journal", strategy);
    }

    public void testXStreamJournal() throws IOException {
        Serializer<Object> strategy = new XStreamSerializer<Object>();

        startAndCrash("journal", strategy);
        recover("journal", strategy);
    }

    public void testSkaringaJournal() throws IOException {
        Serializer<Object> strategy = new SkaringaSerializer<Object>();

        startAndCrash("journal", strategy);
        recover("journal", strategy);
    }

    private void startAndCrash(String suffix, Serializer<Object> journalSerializer) throws IOException {
        Prevayler<StringBuilder> prevayler = createPrevayler(suffix, journalSerializer);

        prevayler.execute(new AppendTransaction(" first"));
        prevayler.execute(new AppendTransaction(" second"));
        prevayler.execute(new AppendTransaction(" third"));
        assertEquals("the system first second third", prevayler.prevalentSystem().toString());
        prevayler.close();
    }

    private void recover(String suffix, Serializer<Object> journalSerializer) throws IOException {
        Prevayler<StringBuilder> prevayler = createPrevayler(suffix, journalSerializer);
        assertEquals("the system first second third", prevayler.prevalentSystem().toString());
    }

    private Prevayler<StringBuilder> createPrevayler(String suffix, Serializer<Object> journalSerializer) throws IOException {
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

    private static class MySerializer implements Serializer<Object> {

        public void writeObject(OutputStream stream, Object object) throws Exception {
            Writer writer = new OutputStreamWriter(stream, "UTF-8");
            AppendTransaction transaction = (AppendTransaction) object;
            writer.write(transaction.toAdd);
            writer.flush();
        }

        public Object readObject(InputStream stream) throws Exception {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            return new AppendTransaction(reader.readLine());
        }

    }

}
