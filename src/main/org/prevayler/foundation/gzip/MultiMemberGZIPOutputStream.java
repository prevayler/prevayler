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

    @Override public void write(int b) throws IOException {
        write(new byte[] { (byte) b });
    }

    @Override public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    @Override public void write(byte b[], int off, int len) throws IOException {
        if (_gzip == null) {
            _gzip = new GZIPOutputStream(_stream);
        }
        _gzip.write(b, off, len);
    }

    @Override public void flush() throws IOException {
        if (_gzip != null) {
            _gzip.close();
            _gzip = null;
        }
        _stream.flush();
    }

    @Override public void close() {
        throw new UnsupportedOperationException();
    }

}
