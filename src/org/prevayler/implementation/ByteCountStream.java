// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import java.io.*;

/** A FileOutputStream that counts the number of bytes written and forces all buffers to synchronize with the underlying device when flushed.
*/
final class ByteCountStream extends FileOutputStream {

    public ByteCountStream(File file) throws IOException {
		super(file);
	}

    public void flush() throws IOException {
	super.flush();   //"The flush method of OutputStream does nothing." - JDK1.3 API documentation. I'm calling it just in case it starts doing something in a future version of FileOutputStream or OutputStream.
	getFD().sync();  //"Force all system buffers to synchronize with the underlying device." - JDK1.3 API documentation.
    }

    public void write(byte[] b) throws IOException {
		super.write(b);
	bytesWritten += b.length;
	}

	public void write(byte[] b, int off, int len) throws IOException {
	super.write(b, off, len);
	bytesWritten += len;
	}

    public void write(int b) throws IOException {
		super.write(b);
	++bytesWritten;
	}

    public long bytesWritten() {
	return bytesWritten;
	}

    private long bytesWritten;
}
