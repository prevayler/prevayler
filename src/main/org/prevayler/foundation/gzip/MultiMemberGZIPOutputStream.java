package org.prevayler.foundation.gzip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class MultiMemberGZIPOutputStream extends OutputStream {

	private OutputStream _stream;
	private GZIPOutputStream _gzip;

	public MultiMemberGZIPOutputStream(OutputStream stream) {
		_stream = new NonCloseableOutputStream(stream);
		_gzip = null;
	}

	public void write(int b) throws IOException {
		write(new byte[]{(byte) b});
	}

	public void write(byte b[]) throws IOException {
		write(b, 0, b.length);
	}

	public void write(byte b[], int off, int len) throws IOException {
		if (_gzip == null) {
			_gzip = new GZIPOutputStream(_stream);
		}
		_gzip.write(b, off, len);
	}

	public void flush() throws IOException {
		if (_gzip != null) {
			_gzip.close();
			_gzip = null;
		}
		_stream.flush();
	}

	public void close() throws IOException {
		throw new UnsupportedOperationException();
	}

}
