//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Carlos Villela.

package org.prevayler.foundation;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.UTFDataFormatException;

import org.prevayler.foundation.monitor.*;


public class SimpleInputStream {

	private final File _file;
	private final ObjectInputStream _delegate;
	private boolean _EOF = false;
    private Monitor _monitor;


	public SimpleInputStream(File file, ClassLoader loader, Monitor monitor) throws IOException {
	    _monitor = monitor;
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
			ignoreStreamCorruption(scx);
		} catch (UTFDataFormatException utfx) {
			ignoreStreamCorruption(utfx);
		} catch (RuntimeException rx) {   //Some stream corruptions cause runtime exceptions in JDK1.3.1!
			ignoreStreamCorruption(rx);
		}

		_delegate.close();
		_EOF = true;
		throw new EOFException();
	}


	private void ignoreStreamCorruption(Exception ex) {
		String message = "Stream corruption found while reading a transaction from the journal. If this is a transaction that was being written when a system crash occurred, there is no problem because it was never executed on the Prevalent System. Before executing each transaction, Prevayler writes it to the journal and calls the java.io.FileDescritor.sync() method to instruct the Java API to physically sync all operating system RAM buffers to disk.";
		_monitor.notify(message, _file, ex);
	}


	public void close() throws IOException {
		_delegate.close();
		_EOF = true;
	}
}
