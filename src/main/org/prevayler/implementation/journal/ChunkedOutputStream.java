package org.prevayler.implementation.journal;

import java.io.IOException;
import java.io.OutputStream;

public class ChunkedOutputStream {

	private static final byte[] CRLF = new byte[] {'\r', '\n'};

	private OutputStream stream;

	public ChunkedOutputStream(OutputStream stream) {
		this.stream = stream;
	}

	public void writeChunk(byte[] bytes) throws IOException {
		stream.write(Integer.toHexString(bytes.length).toUpperCase().getBytes("US-ASCII"));
		stream.write(CRLF);
		stream.write(bytes);
		stream.write(CRLF);
	}

}
