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
import org.prevayler.Transaction;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.journal.JournalError;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

public class SkipOldTransactionsTest extends FileIOTest {

    public void testSkipOldTransactions() throws IOException {
        Prevayler<StringBuilder> original = createPrevayler("MyJournal", new MySerializer(false));

        original.execute(new AppendTransaction(" first"));
        original.execute(new AppendTransaction(" second"));

        original.takeSnapshot();

        original.execute(new AppendTransaction(" third"));
        assertEquals("the system first second third", original.prevalentSystem().toString());
        original.close();

        String expected = "6;systemVersion=1;executionTime=1000002\r\n";
        expected += " first\r\n";
        expected += "7;systemVersion=2;executionTime=1000004\r\n";
        expected += " second\r\n";
        expected += "6;systemVersion=3;executionTime=1000006\r\n";
        expected += " third\r\n";
        assertEquals(expected, journalContents("MyJournal"));

        Prevayler<StringBuilder> recovered = createPrevayler("MyJournal", new MySerializer(true));
        assertEquals("the system first second third", recovered.prevalentSystem().toString());
    }

    public void testDetectOldJournalSuffix() throws IOException {
        Prevayler<StringBuilder> original = createPrevayler("OldJournal", new MySerializer(false));

        original.execute(new AppendTransaction(" first"));
        original.execute(new AppendTransaction(" second"));

        original.takeSnapshot();

        original.execute(new AppendTransaction(" third"));
        assertEquals("the system first second third", original.prevalentSystem().toString());
        original.close();

        String expected = "6;systemVersion=1;executionTime=1000002\r\n";
        expected += " first\r\n";
        expected += "7;systemVersion=2;executionTime=1000004\r\n";
        expected += " second\r\n";
        expected += "6;systemVersion=3;executionTime=1000006\r\n";
        expected += " third\r\n";
        assertEquals(expected, journalContents("OldJournal"));

        try {
            createPrevayler("NewJournal", new MySerializer(true));
            fail();
        } catch (JournalError expectedError) {
            File journal = new PrevaylerDirectory(_testDirectory).journalFile(1, "OldJournal");
            assertEquals("There are transactions needing to be recovered from " + journal + ", but only NewJournal files are supported", expectedError.getMessage());
        }
    }

    public void testAllowOldJournalSuffix() throws IOException {
        Prevayler<StringBuilder> original = createPrevayler("OldJournal", new MySerializer(false));

        original.execute(new AppendTransaction(" first"));
        original.execute(new AppendTransaction(" second"));
        original.execute(new AppendTransaction(" third"));
        original.takeSnapshot();

        assertEquals("the system first second third", original.prevalentSystem().toString());
        original.close();

        String expected = "6;systemVersion=1;executionTime=1000002\r\n";
        expected += " first\r\n";
        expected += "7;systemVersion=2;executionTime=1000004\r\n";
        expected += " second\r\n";
        expected += "6;systemVersion=3;executionTime=1000006\r\n";
        expected += " third\r\n";
        assertEquals(expected, journalContents("OldJournal"));

        Prevayler<StringBuilder> recovered = createPrevayler("NewJournal", new MySerializer(true));
        assertEquals("the system first second third", recovered.prevalentSystem().toString());
    }

    private Prevayler<StringBuilder> createPrevayler(String suffix, Serializer<Transaction> journalSerializer) throws IOException {
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

    private static class MySerializer implements Serializer<Transaction> {

        private boolean afterSnapshot;

        public MySerializer(boolean afterSnapshot) {
            this.afterSnapshot = afterSnapshot;
        }

        public void writeObject(OutputStream stream, Transaction object) throws Exception {
            Writer writer = new OutputStreamWriter(stream, "UTF-8");
            AppendTransaction transaction = (AppendTransaction) object;
            writer.write(transaction.toAdd);
            writer.flush();
        }

        public Transaction readObject(InputStream stream) throws Exception {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String toAdd = reader.readLine();
            if (afterSnapshot) {
                assertFalse("Shouldn't have recovered transaction from before snapshot", toAdd.equals(" first") || toAdd.equals(" second"));
            }
            return new AppendTransaction(toAdd);
        }

    }

}
