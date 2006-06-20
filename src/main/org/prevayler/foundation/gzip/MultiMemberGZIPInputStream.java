// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.foundation.gzip;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class MultiMemberGZIPInputStream extends InputStream {

    private ContinuableGZIPInputStream _gzip;

    public MultiMemberGZIPInputStream(InputStream stream) throws IOException {
        _gzip = new ContinuableGZIPInputStream(stream);
    }

    @Override public int available() throws IOException {
        return _gzip.available();
    }

    @Override public int read() throws IOException {
        byte[] buf = new byte[1];
        int n = read(buf);
        return n == -1 ? -1 : buf[0];
    }

    @Override public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    @Override public int read(byte b[], int off, int len) throws IOException {
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

    @Override public void close() {
        throw new UnsupportedOperationException();
    }

    @Override public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override public boolean markSupported() {
        return false;
    }

    @Override public void mark(@SuppressWarnings("unused") int readlimit) {
        throw new UnsupportedOperationException();
    }

    @Override public long skip(@SuppressWarnings("unused") long n) {
        throw new UnsupportedOperationException();
    }

}
