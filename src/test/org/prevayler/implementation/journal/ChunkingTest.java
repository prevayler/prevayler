package org.prevayler.implementation.journal;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ChunkingTest extends TestCase {

	public void testChunkedOutput() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		Chunking.writeChunk(bytes, new Chunk("abcdefghijklmno".getBytes("US-ASCII")));
		assertEquals("F\r\nabcdefghijklmno\r\n", bytes.toString("US-ASCII"));
	}

	public void testChunkedInput() throws IOException {
		ByteArrayInputStream bytes = new ByteArrayInputStream("F\r\nabcdefghijklmno\r\n".getBytes());
		assertEquals("abcdefghijklmno", new String(Chunking.readChunk(bytes).getBytes(), "US-ASCII"));
	}

	public void testMultipleChunks() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		Chunking.writeChunk(output, new Chunk("foo".getBytes("US-ASCII")));
		Chunking.writeChunk(output, new Chunk("bar".getBytes("US-ASCII")));
		Chunking.writeChunk(output, new Chunk("".getBytes("US-ASCII")));
		Chunking.writeChunk(output, new Chunk("zot".getBytes("US-ASCII")));

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());

		assertEquals("foo", new String(Chunking.readChunk(input).getBytes(), "US-ASCII"));
		assertEquals("bar", new String(Chunking.readChunk(input).getBytes(), "US-ASCII"));
		assertEquals("", new String(Chunking.readChunk(input).getBytes(), "US-ASCII"));
		assertEquals("zot", new String(Chunking.readChunk(input).getBytes(), "US-ASCII"));
		assertNull(Chunking.readChunk(input));
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
		try {
			Chunking.readChunk(bytes);
			fail("Should have thrown IOException");
		} catch (IOException exception) {
			assertEquals(message, exception.getMessage());
		}
	}

	public void testParameters() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		Chunk chunkOut = new Chunk("foo".getBytes("US-ASCII"));
		chunkOut.setParameter("one", "uno");
		chunkOut.setParameter("two", "dos");
		Chunking.writeChunk(output, chunkOut);

		assertEquals("3;one=uno;two=dos\r\nfoo\r\n", output.toString("US-ASCII"));

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());

		Chunk chunkIn = Chunking.readChunk(input);

		assertEquals("foo", new String(chunkIn.getBytes(), "US-ASCII"));
		assertEquals("uno", chunkIn.getParameter("one"));
		assertEquals("dos", chunkIn.getParameter("two"));
	}

}
