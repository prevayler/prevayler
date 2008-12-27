package org.prevayler.foundation.gzip;

import org.prevayler.foundation.gzip.ContinuableGZIPInputStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class MultiMemberGZIPInputStream extends InputStream {

	private ContinuableGZIPInputStream _gzip;

	public MultiMemberGZIPInputStream(InputStream stream) throws IOException {
		_gzip = new ContinuableGZIPInputStream(stream);
	}

	public int available() throws IOException {
		return _gzip.available();
	}

	public int read() throws IOException {
		byte[] buf = new byte[1];
		int n = read(buf);
		return n == -1 ? -1 : buf[0];
	}

	public int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}

	public int read(byte b[], int off, int len) throws IOException {
		int n = _gzip.read(b, off, len);
		if (n == -1) {
			try {
				_gzip = new ContinuableGZIPInputStream(_gzip.remainingInput());
			} catch (EOFException e) {
				return -1;
			}
			return _gzip.read(b, off, len);
		}
		return n;
	}

	public void close() throws IOException {
		throw new UnsupportedOperationException();
	}

	public void reset() throws IOException {
		throw new UnsupportedOperationException();
	}

	public boolean markSupported() {
		return false;
	}

	public void mark(int readlimit) {
		throw new UnsupportedOperationException();
	}

	public long skip(long n) throws IOException {
		throw new UnsupportedOperationException();
	}

}
