package org.prevayler.foundation.serialization;

import java.io.IOException;

public class XStreamSerializationTest extends SerializationTestCase {

	protected SerializationStrategy createStrategy() {
		return new XStreamSerializationStrategy();
	}

	public void testOneObject() throws IOException, ClassNotFoundException {
		writeObject("a string to be written");

		assertSerializedAs("<string>a string to be written</string>\n" +
				"<!--PREVAYLER RULES!-->\n");

		createDeserializer();

		assertNextObject("a string to be written");
	}

	public void testManyObjects() throws IOException, ClassNotFoundException {
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

}
