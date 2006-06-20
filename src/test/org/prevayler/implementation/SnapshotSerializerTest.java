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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.AssertionFailedError;

public class SnapshotSerializerTest extends FileIOTest {

    public void testConfigureSnapshotSerializer() throws IOException {
        Serializer serializer = new MySerializer();

        takeSnapshot(serializer);

        assertEquals("Yes, this is MySerializationStrategy!\n" + "the system first second third\n", snapshotContents());

        recover(serializer);
    }

    public void testBadSuffix() {
        PrevaylerFactory factory = new PrevaylerFactory();
        try {
            factory.configureSnapshotSerializer("SNAPSHOT", new JavaSerializer());
            fail();
        } catch (IllegalArgumentException exception) {
            assertEquals("Snapshot filename suffix must match /[a-zA-Z0-9]*[Ss]napshot/, but 'SNAPSHOT' does not", exception.getMessage());
        }
    }

    public void testXStreamSnapshot() throws IOException {
        Serializer serializer = new XStreamSerializer();

        takeSnapshot(serializer);
        recover(serializer);
    }

    public void testSkaringaSnapshot() throws IOException {
        Serializer serializer = new SkaringaSerializer();

        takeSnapshot(serializer);
        recover(serializer);
    }

    private void takeSnapshot(Serializer snapshotSerializer) throws IOException {
        Prevayler<StringBuilder> prevayler = createPrevayler(snapshotSerializer);

        prevayler.execute(new AppendTransaction(" first"));
        prevayler.execute(new AppendTransaction(" second"));
        prevayler.execute(new AppendTransaction(" third"));
        assertEquals("the system first second third", prevayler.prevalentSystem().toString());

        prevayler.takeSnapshot();
        prevayler.close();
    }

    private void recover(Serializer snapshotSerializer) throws IOException {
        Prevayler<StringBuilder> prevayler = createPrevayler(snapshotSerializer);
        assertEquals("the system first second third", prevayler.prevalentSystem().toString());
    }

    private Prevayler<StringBuilder> createPrevayler(Serializer snapshotSerializer) throws IOException {
        PrevaylerFactory<StringBuilder> factory = new PrevaylerFactory<StringBuilder>();
        factory.configurePrevalentSystem(new StringBuilder("the system"));
        factory.configurePrevalenceDirectory(_testDirectory);
        factory.configureSnapshotSerializer("snapshot", snapshotSerializer);
        return factory.create();
    }

    private String snapshotContents() throws IOException {
        File snapshot = new File(_testDirectory).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".snapshot");
            }
        })[0];

        FileReader file = new FileReader(snapshot);
        StringWriter string = new StringWriter();

        int n;
        char[] c = new char[1024];
        while ((n = file.read(c)) != -1) {
            string.write(c, 0, n);
        }

        file.close();

        return string.toString();
    }

    private static class MySerializer implements Serializer {

        public void writeObject(OutputStream stream, Object object) throws Exception {
            StringBuilder system = (StringBuilder) object;
            Writer writer = new OutputStreamWriter(stream, "UTF-8");
            writer.write("Yes, this is MySerializationStrategy!\n");
            writer.write(system.toString());
            writer.write('\n');
            writer.flush();
        }

        public Object readObject(InputStream stream) throws Exception {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String prolog = reader.readLine();
            if ("Yes, this is MySerializationStrategy!".equals(prolog)) {
                String contents = reader.readLine();
                return new StringBuilder(contents);
            } else {
                throw new AssertionFailedError("got prolog=" + prolog);
            }
        }

    }

}
