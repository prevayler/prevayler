package org.prevayler.implementation.journal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ChunkedInputStream {

	private InputStream stream;

	public ChunkedInputStream(InputStream stream) {
		this.stream = stream;
	}

	public byte[] readChunk() throws IOException {
		String header = readLine();

		if (header == null) {
			return null;
		}

		if (!header.matches("(0|[1-9A-F][0-9A-F]{0,6}|[1-7][0-9A-F]{7})\r\n")) {
			throw new IOException("Chunk header corrupted");
		}

		int length = Integer.parseInt(header.trim(), 16);
		byte[] chunk = new byte[length];

		int total = 0;
		while (total < length) {
			int read = stream.read(chunk, total, length - total);
			if (read == -1) {
				throw new IOException("Unexpected end of stream in chunk data");
			}
			total += read;
		}

		if (stream.read() != '\r' || stream.read() != '\n') {
			throw new IOException("Chunk trailer corrupted");
		}

		return chunk;
	}

	private String readLine() throws IOException {
		ByteArrayOutputStream header = new ByteArrayOutputStream();
		while (true) {
			int b = stream.read();
			if (b == -1) {
				if (header.size() == 0) {
					return null;
				} else {
					throw new IOException("Unexpected end of stream in chunk header");
				}
			}
			header.write(b);
			if (b == '\n') {
				return header.toString("US-ASCII");
			}
		}
	}

}
