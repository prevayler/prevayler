// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.foundation;

import java.io.*;

public class SimpleOutputStream {

	private final File _file;
	private final ObjectOutputStream _delegate;
	private final FileDescriptor _fileDescriptor;
	private boolean _isValid = true;


	public SimpleOutputStream(File file) throws IOException {
		_file = file;
		FileOutputStream fileStream = new FileOutputStream(file);
		_fileDescriptor = fileStream.getFD();
		_delegate = new ObjectOutputStream(fileStream);
	}


	public boolean isValid() { return _isValid; }


	public void writeObject(Object object) throws IOException {
		_delegate.writeObject(object);
	}


	public void sync() throws IOException {
		_fileDescriptor.sync();
	}


	public void close() throws IOException {
		_delegate.close();
		_isValid = false;
	}

	public File file() { return _file; }

}
