//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Carlos Villela.

package org.prevayler.foundation;

import org.prevayler.foundation.monitor.Monitor;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.UTFDataFormatException;

public class DurableInputStream {

	private final File _file;
	private boolean _EOF = false;
	private Monitor _monitor;
	private InputStream _fileStream;


	public DurableInputStream(File file, Monitor monitor) throws IOException {
		_monitor = monitor;
		_file = file;
		_fileStream = new BufferedInputStream(new FileInputStream(file));
	}


	public Chunk readChunk() throws IOException {
		if (_EOF) throw new EOFException();

		try {
			Chunk chunk = Chunking.readChunk(_fileStream);
			if (chunk != null) return chunk;
		} catch (EOFException eofx) {
			// Do nothing.
		} catch (ObjectStreamException scx) {
			ignoreStreamCorruption(scx);
		} catch (UTFDataFormatException utfx) {
			ignoreStreamCorruption(utfx);
		} catch (RuntimeException rx) {   //Some stream corruptions cause runtime exceptions in JDK1.3.1!
			ignoreStreamCorruption(rx);
		}

		_fileStream.close();
		_EOF = true;
		throw new EOFException();
	}


	private void ignoreStreamCorruption(Exception ex) {
		String message = "Stream corruption found while reading a transaction from the journal. If this is a transaction that was being written when a system crash occurred, there is no problem because it was never executed on the Prevalent System. Before executing each transaction, Prevayler writes it to the journal and calls the java.io.FileDescritor.sync() method to instruct the Java API to physically sync all operating system RAM buffers to disk.";
		_monitor.notify(this.getClass(), message, _file, ex);
	}


	public void close() throws IOException {
		_fileStream.close();
		_EOF = true;
	}

}
