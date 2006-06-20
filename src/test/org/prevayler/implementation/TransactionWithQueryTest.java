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

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.FileIOTest;
import org.prevayler.foundation.serialization.JavaSerializer;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.foundation.serialization.SkaringaSerializer;
import org.prevayler.foundation.serialization.XStreamSerializer;

public class TransactionWithQueryTest extends FileIOTest {

    public void testJavaJournal() throws Exception {
        Serializer<Object> strategy = new JavaSerializer<Object>();

        startAndCrash(strategy);
        recover(strategy);
    }

    public void testXStreamJournal() throws Exception {
        Serializer<Object> strategy = new XStreamSerializer<Object>();

        startAndCrash(strategy);
        recover(strategy);
    }

    public void testSkaringaJournal() throws Exception {
        Serializer<Object> strategy = new SkaringaSerializer<Object>();

        startAndCrash(strategy);
        recover(strategy);
    }

    private void startAndCrash(Serializer<Object> journalSerializer) throws Exception {
        Prevayler<StringBuilder> prevayler = createPrevayler(journalSerializer);

        assertEquals("the system first", prevayler.execute(new AppendTransactionWithQuery(" first")));
        assertEquals("the system first second", prevayler.execute(new AppendTransactionWithQuery(" second")));
        assertEquals("the system first second third", prevayler.execute(new AppendTransactionWithQuery(" third")));
        assertEquals("the system first second third", prevayler.prevalentSystem().toString());

        prevayler.close();
    }

    private void recover(Serializer<Object> journalSerializer) throws Exception {
        Prevayler<StringBuilder> prevayler = createPrevayler(journalSerializer);
        assertEquals("the system first second third", prevayler.prevalentSystem().toString());
    }

    private Prevayler<StringBuilder> createPrevayler(Serializer<Object> journalSerializer) throws Exception {
        PrevaylerFactory<StringBuilder> factory = new PrevaylerFactory<StringBuilder>();
        factory.configurePrevalentSystem(new StringBuilder("the system"));
        factory.configurePrevalenceDirectory(_testDirectory);
        factory.configureJournalSerializer("journal", journalSerializer);
        return factory.create();
    }

}
