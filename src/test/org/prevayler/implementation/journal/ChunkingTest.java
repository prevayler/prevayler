package org.prevayler.implementation.journal;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChunkingTest extends TestCase {

	public void testChunkedOutput() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ChunkedOutputStream chunked = new ChunkedOutputStream(bytes);
		chunked.writeChunk("abcdefghijklmno".getBytes("US-ASCII"));
		assertEquals("F\r\nabcdefghijklmno\r\n", bytes.toString("US-ASCII"));
	}

	public void testChunkedInput() throws IOException {
		ByteArrayInputStream bytes = new ByteArrayInputStream("F\r\nabcdefghijklmno\r\n".getBytes());
		ChunkedInputStream chunked = new ChunkedInputStream(bytes);
		assertEquals("abcdefghijklmno", new String(chunked.readChunk(), "US-ASCII"));
	}

	public void testMultipleChunks() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ChunkedOutputStream output = new ChunkedOutputStream(bytes);

		output.writeChunk("foo".getBytes("US-ASCII"));
		output.writeChunk("bar".getBytes("US-ASCII"));
		output.writeChunk("".getBytes("US-ASCII"));
		output.writeChunk("zot".getBytes("US-ASCII"));

		ChunkedInputStream input = new ChunkedInputStream(new ByteArrayInputStream(bytes.toByteArray()));

		assertEquals("foo", new String(input.readChunk(), "US-ASCII"));
		assertEquals("bar", new String(input.readChunk(), "US-ASCII"));
		assertEquals("", new String(input.readChunk(), "US-ASCII"));
		assertEquals("zot", new String(input.readChunk(), "US-ASCII"));
		assertNull(input.readChunk());
	}

	public void testMalformed() throws IOException {
		checkMalformed("3\nfoo\r\n", "Chunk header corrupted");
		checkMalformed("3\rfoo\r\n", "Chunk header corrupted");
		checkMalformed("03\r\nfoo\r\n", "Chunk header corrupted");
		checkMalformed("f\r\nabcdefghijklmno\r\n", "Chunk header corrupted");
		checkMalformed("FFF\r\nabcdefghijklmno\r\n", "Unexpected end of stream in chunk data");
		checkMalformed("FFF", "Unexpected end of stream in chunk header");
		checkMalformed("F\r\nabcdefghijklmno", "Chunk trailer corrupted");
		checkMalformed("F\r\nabcdefghijklmno\n", "Chunk trailer corrupted");
		checkMalformed("F\r\nabcdefghijklmno\r", "Chunk trailer corrupted");
	}

	private void checkMalformed(String input, String message) throws IOException {
		ByteArrayInputStream bytes = new ByteArrayInputStream(input.getBytes("US-ASCII"));
		ChunkedInputStream chunked = new ChunkedInputStream(bytes);
		try {
			chunked.readChunk();
			fail("Should have thrown IOException");
		} catch (IOException exception) {
			assertEquals(message, exception.getMessage());
		}
	}

	public void testParameters() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ChunkedOutputStream output = new ChunkedOutputStream(bytes);

		Map parameters = new LinkedHashMap();
		parameters.put("one", "uno");
		parameters.put("two", "dos");
		output.writeChunk("foo".getBytes("US-ASCII"), parameters);

		assertEquals("3;one=uno;two=dos\r\nfoo\r\n", bytes.toString("US-ASCII"));

		ChunkedInputStream input = new ChunkedInputStream(new ByteArrayInputStream(bytes.toByteArray()));

		assertEquals("foo", new String(input.readChunk(), "US-ASCII"));
		assertEquals(parameters, input.getParameters());
	}

}
