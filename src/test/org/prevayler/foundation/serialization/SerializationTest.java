package org.prevayler.foundation.serialization;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;

public abstract class SerializationTest extends TestCase {

	private SerializationStrategy strategy;
	private ByteArrayOutputStream out;
	private Serializer serializer;
	private Deserializer deserializer;

	protected void setUp() throws IOException {
		strategy = createStrategy();
		out = new ByteArrayOutputStream();
		serializer = strategy.createSerializer(out);
	}

	protected abstract SerializationStrategy createStrategy();

	protected void writeObject(Object original) throws IOException {
		serializer.writeObject(original);
		serializer.flush();
	}

	protected void createDeserializer() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		deserializer = strategy.createDeserializer(in);
	}

	protected void assertSerializedAs(String serializedForm) {
		assertEquals(serializedForm, new String(out.toString()));
	}

	protected void assertNextObject(Object original) throws IOException, ClassNotFoundException {
		assertEquals(original, deserializer.readObject());
	}

	protected void assertEOF() throws IOException, ClassNotFoundException {
		try {
			deserializer.readObject();
			fail();
		} catch (EOFException eof) {
		}
	}

}
