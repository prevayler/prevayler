package org.prevayler.implementation.journal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class ChunkedOutputStream {

	private static final String ASCII = "US-ASCII";
	private static final byte[] CRLF = new byte[] {'\r', '\n'};

	private OutputStream stream;

	public ChunkedOutputStream(OutputStream stream) {
		this.stream = stream;
	}

	public void writeChunk(byte[] bytes) throws IOException {
		writeChunk(bytes, Collections.EMPTY_MAP);
	}

	public void writeChunk(byte[] bytes, Map parameters) throws IOException {
		stream.write(Integer.toHexString(bytes.length).toUpperCase().getBytes(ASCII));
		Iterator iterator = parameters.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String name = (String) entry.getKey();
			String value = (String) entry.getValue();
			if (!ChunkedInputStream.validToken(name)) {
				throw new IOException("Invalid parameter name '" + name + "'");
			}
			if (!ChunkedInputStream.validToken(value)) {
				throw new IOException("Invalid parameter value '" + value + "'");
			}
			stream.write(';');
			stream.write(name.getBytes(ASCII));
			stream.write('=');
			stream.write(value.getBytes(ASCII));
		}
		stream.write(CRLF);
		stream.write(bytes);
		stream.write(CRLF);
	}

}
