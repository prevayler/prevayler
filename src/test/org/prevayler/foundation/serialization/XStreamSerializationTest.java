package org.prevayler.foundation.serialization;

import java.io.IOException;

public class XStreamSerializationTest extends SerializationTest {

	protected JournalSerializationStrategy createStrategy() {
		return new JournalSerializationStrategy(new XStreamSerializer());
	}

	public void testOneObject() throws IOException, ClassNotFoundException {
		writeObject("a string to be written");

		assertSerializedAs("27\r\n<string>a string to be written</string>\r\n");

		createDeserializer();

		assertNextObject("a string to be written");
	}

	public void testManyObjects() throws IOException, ClassNotFoundException {
		writeObject("first string");
		writeObject("second string");
		writeObject("third string");

		assertSerializedAs("1D\r\n<string>first string</string>\r\n" +
				"1E\r\n<string>second string</string>\r\n" +
				"1D\r\n<string>third string</string>\r\n");

		createDeserializer();

		assertNextObject("first string");
		assertNextObject("second string");
		assertNextObject("third string");
		assertEOF();
	}

}
