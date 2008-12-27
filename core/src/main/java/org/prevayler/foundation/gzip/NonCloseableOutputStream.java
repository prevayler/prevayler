package org.prevayler.foundation.gzip;

import java.io.IOException;
import java.io.OutputStream;

public class NonCloseableOutputStream extends OutputStream {

	private final OutputStream _stream;

	public NonCloseableOutputStream(OutputStream stream) {
		_stream = stream;
	}

	public void close() throws IOException {
	}

	public void flush() throws IOException {
		_stream.flush();
	}

	public void write(int b) throws IOException {
		_stream.write(b);
	}

	public void write(byte b[]) throws IOException {
		_stream.write(b);
	}

	public void write(byte b[], int off, int len) throws IOException {
		_stream.write(b, off, len);
	}

}
