//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.foundation;

import java.io.*;


public class DurableOutputStream {
	private static final int NOT_CLOSED = 0;
	private static final int CLOSE_CALLED = 1;
	private static final int REALLY_CLOSED = 2;

	private final File _file;
	private final ObjectOutputStream _objectOutputStream;
	private final FileOutputStream _fileOutputStream;
	private final FileDescriptor _fileDescriptor;

	private IOException _exceptionWhileSynching;
	private IOException _exceptionWhileClosing;

	private int _objectsWritten = 0;
	private int _objectsSynced = 0;
	private int _fileSyncCount = 0;

	private int _closingState = NOT_CLOSED;

	public DurableOutputStream(File file) throws IOException {
		_file = file;
		_fileOutputStream = new FileOutputStream(file);
		_fileDescriptor = _fileOutputStream.getFD();
		_objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(_fileOutputStream, 16 * 512)); //Arbitrarily large buffer. Should be a power of two multiplied by 512 bytes (disk sector size).

		startSyncher();
	}

	private void startSyncher() {
		Thread syncher = new Thread() {
			public void run() {
				syncher();
			}
		};
		syncher.setDaemon(true);
		syncher.start();
	}


	public void sync(Object object, Turn myTurn) throws IOException {
		int thisWrite;

		try {
			myTurn.start();
			thisWrite = writeObject(object);
		} finally {
			myTurn.end();
		}

		waitUntilSynced(thisWrite);
	}

	private synchronized int writeObject(Object object) throws IOException {
		if (_closingState != NOT_CLOSED) {
			throw new IOException("stream is closing");
		}

		_objectOutputStream.writeObject(object);
		_objectsWritten++;
		notifyAll();
		return _objectsWritten;
	}

	private synchronized void waitUntilSynced(int thisWrite) throws IOException {
		while (_objectsSynced < thisWrite && _exceptionWhileSynching == null) {
			Cool.wait(this);
		}
		if (_objectsSynced < thisWrite) {
			throw _exceptionWhileSynching;
		}
	}

	public synchronized void close() throws IOException {
		if (_closingState == NOT_CLOSED) {
			_closingState = CLOSE_CALLED;
			notifyAll();
		}

		while (_closingState != REALLY_CLOSED) {
			Cool.wait(this);
		}

		if (_exceptionWhileClosing != null) {
			throw _exceptionWhileClosing;
		}
	}

	private synchronized void syncher() {
		try {
			while (true) {
				while (_objectsSynced == _objectsWritten && _closingState == NOT_CLOSED) {
					Cool.wait(this);
				}

				if (_objectsSynced == _objectsWritten) {
					break;
				}

				_objectOutputStream.flush();

				_fileOutputStream.flush();
				// ObjectOutputStream > BufferedOutputStream > FileOutputStream.
				// The ObjectOutputStream will flush its underlying stream,
				// but the BufferedOutputStream API documentation (JDK1.4.1_01)
				// does not specify that it must flush its underlying stream too. :P
				// The above line is here in case some obscure implementation doesn't
				// flush the underlying stream.

				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				// The above line should not be necessary but, with J2RE1.4.0_01 for Windows
				// it actually produces 5x better results (!) for the
				// org.prevayler.demos.scalability transaction execution test.
				// NOT TESTED SINCE REFACTORING

				_fileDescriptor.sync();

				Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

				_objectsSynced = _objectsWritten;
				_fileSyncCount++;

				notifyAll();
			}
		} catch (IOException duringSync) {
			_exceptionWhileSynching = duringSync;
		} finally {
			try {
				_objectOutputStream.close();
			} catch (IOException duringClose) {
				_exceptionWhileClosing = duringClose;
			}
			_closingState = REALLY_CLOSED;
			notifyAll();
		}
	}


	public File file() {
		return _file;
	}

	public synchronized int fileSyncCount() {
		return _fileSyncCount;
	}

	public synchronized boolean reallyClosed() {
		return _closingState == REALLY_CLOSED;
	}

}
