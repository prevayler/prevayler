// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.foundation;

import java.io.*;

public class SimpleInputStream {

	private final File _file;
	private final ObjectInputStream _delegate;
	private boolean _EOF = false;


	public SimpleInputStream(File file) throws IOException {
		System.out.println("Reading " + file + "...");
		_file = file;
		_delegate = new ObjectInputStream(new FileInputStream(file));
	}


	public Object readObject() throws IOException, ClassNotFoundException {
		if (_EOF) throw new EOFException();

		try {
			return _delegate.readObject();
		} catch (EOFException eofx) {
			// Do nothing.
		} catch (ObjectStreamException scx) {
			message(scx);
		} catch (UTFDataFormatException utfx) {
			message(utfx);
		} catch (RuntimeException rx) {   //Some stream corruptions cause runtime exceptions in JDK1.3.1!
			message(rx);
		}

		_delegate.close();
		_EOF = true;
		throw new EOFException();
	}


	public void close() throws IOException {
		_delegate.close();
		_EOF = true;
	}

	public long countObjectsLeft() throws IOException, ClassNotFoundException {
		long result = 0;
		while (true) {
			try {
				readObject();
			} catch (EOFException eof) {
				return result;
			}
			result++;
		}	
	}

	private void message(Exception exception) {
		System.out.println(
			"\n" + exception + " (File: " + _file + ")" +
			"\n   The above is a stream corruption that can be caused by:" +
			"\n      - A system crash while writing to the file (that is OK)." +
			"\n      - A corruption in the file system (that is NOT OK)." +
			"\n      - Tampering with the file (that is NOT OK)."
		);
	}
}
