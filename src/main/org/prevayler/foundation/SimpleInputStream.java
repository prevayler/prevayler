//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.foundation;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.UTFDataFormatException;

import org.prevayler.Monitor;

public class SimpleInputStream {

	private final File _file;
	private final ObjectInputStream _delegate;
	private boolean _EOF = false;
    private Monitor _monitor;


	public SimpleInputStream(File file, ClassLoader loader, Monitor monitor) throws IOException {
	    _monitor = monitor;
		_monitor.readingTransactionLogFile(file, loader);
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
			_monitor.ignoringStreamCorruption(scx, _file);
		} catch (UTFDataFormatException utfx) {
		    _monitor.ignoringStreamCorruption(utfx, _file);
		} catch (RuntimeException rx) {   //Some stream corruptions cause runtime exceptions in JDK1.3.1!
		    _monitor.ignoringStreamCorruption(rx, _file);
		}

		_delegate.close();
		_EOF = true;
		throw new EOFException();
	}


	public void close() throws IOException {
		_delegate.close();
		_EOF = true;
	}
}
