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

public class NonCloseableOutputStream extends OutputStream {

    private final OutputStream _stream;

    public NonCloseableOutputStream(OutputStream stream) {
        _stream = stream;
    }

    @Override public void close() {
    }

    @Override public void flush() throws IOException {
        _stream.flush();
    }

    @Override public void write(int b) throws IOException {
        _stream.write(b);
    }

    @Override public void write(byte b[]) throws IOException {
        _stream.write(b);
    }

    @Override public void write(byte b[], int off, int len) throws IOException {
        _stream.write(b, off, len);
    }

}
