package org.prevayler.foundation.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GZIPSerializationTest extends SerializationTest {

	protected SerializationStrategy createStrategy() {
		return new GZIPSerializationStrategy(new XStreamSerializationStrategy());
	}

	public void testMultiMemberGZIPStreams() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		OutputStream gzip = new MultiMemberGZIPOutputStream(bytes);
		gzip.write(123);
		gzip.flush();
		String expectedOneByteOutput = "1f,8b,8,0,0,0,0,0,0,0,ab,6,0,39,47,d5,15,1,0,0,0";
		assertEquals(expectedOneByteOutput, toHex(bytes.toByteArray()));
		gzip.flush();
		assertEquals(expectedOneByteOutput, toHex(bytes.toByteArray()));
		gzip.write(123);
		gzip.flush();
		byte[] twoGZIPMembers = bytes.toByteArray();
		assertEquals(expectedOneByteOutput + "," + expectedOneByteOutput, toHex(twoGZIPMembers));

		InputStream in = new MultiMemberGZIPInputStream(new ByteArrayInputStream(twoGZIPMembers));
		assertEquals(123, in.read());
		assertEquals(123, in.read());
		assertEquals(-1, in.read());
	}

	private String toHex(byte[] beforeClose) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < beforeClose.length; i++) {
			byte b = beforeClose[i];
			if (i > 0) buf.append(",");
			buf.append(Integer.toHexString(((int) b) & 0xFF));
		}
		return buf.toString();
	}

	public void testOneObject() throws IOException, ClassNotFoundException {
		writeObject("a string to be written");

		createDeserializer();

		assertNextObject("a string to be written");
	}

	public void testManyObjects() throws IOException, ClassNotFoundException {
		writeObject("first string");
		writeObject("second string");
		writeObject("third string");

		createDeserializer();

		assertNextObject("first string");
		assertNextObject("second string");
		assertNextObject("third string");
		assertEOF();
	}

}
