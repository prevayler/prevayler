package org.prevayler.foundation.serialization;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class XStreamSerializationTest extends TestCase {

	private XStreamSerializationStrategy strategy;
	private ByteArrayOutputStream out;
	private Serializer serializer;
	private Deserializer deserializer;

	public void testOneObject() throws IOException, ClassNotFoundException {
		createSerializer();

		writeObject("a string to be written");

		assertSerializedAs("<string>a string to be written</string>\n" +
				"<!--PREVAYLER RULES!-->\n");

		createDeserializer();

		assertNextObject("a string to be written");
	}

	public void testManyObjects() throws IOException, ClassNotFoundException {
		createSerializer();

		writeObject("first string");
		writeObject("second string");
		writeObject("third string");

		assertSerializedAs("<string>first string</string>\n" +
				"<!--PREVAYLER RULES!-->\n" +
				"<string>second string</string>\n" +
				"<!--PREVAYLER RULES!-->\n" +
				"<string>third string</string>\n" +
				"<!--PREVAYLER RULES!-->\n");

		createDeserializer();

		assertNextObject("first string");
		assertNextObject("second string");
		assertNextObject("third string");
		assertEOF();
	}

	private void createSerializer() throws IOException {
		strategy = new XStreamSerializationStrategy();
		out = new ByteArrayOutputStream();
		serializer = strategy.createSerializer(out);
	}

	private void writeObject(String original) throws IOException {
		serializer.writeObject(original);
		serializer.flush();
	}

	private void createDeserializer() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		deserializer = strategy.createDeserializer(in);
	}

	private void assertSerializedAs(String serializedForm) {
		assertEquals(serializedForm, new String(out.toString()));
	}

	private void assertNextObject(String original) throws IOException, ClassNotFoundException {
		assertEquals(original, deserializer.readObject());
	}

	private void assertEOF() throws IOException, ClassNotFoundException {
		try {
			deserializer.readObject();
			fail();
		} catch (EOFException eof) {
		}
	}

}
