//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.foundation;

import java.io.*;

public class SimpleInputStream {

	private final File _file;
	private final ObjectInputStream _delegate;
	private boolean _EOF = false;

	public SimpleInputStream(File file, ClassLoader loader) throws IOException {
		System.out.println("Reading " + file + " using ClassLoader " + loader.toString() + " ...");
		_file = file;
		_delegate = new ObjectInputStreamWithClassLoader(new FileInputStream(file), loader);
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

	private void message(Exception exception) {
		exception.printStackTrace();
		System.err.println(
			"\n   Thrown while reading file: " + _file + ")" +
			"\n   The above is a stream corruption that can be caused by:" +
			"\n      - A system crash while writing to the file (that is OK)." +
			"\n      - A corruption in the file system (that is NOT OK)." +
			"\n      - Tampering with the file (that is NOT OK)."
		);
	}
}
