//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.foundation;

import java.io.*;


public class DurableOutputStream {

	private final File _file;
	private final ObjectOutputStream _objectOutputStream;
	private final FileOutputStream _fileOutputStream;
	private final FileDescriptor _fileDescriptor;

	private volatile SyncBatch _nextBatchToSync = new SyncBatch();
	private final Object _syncherMonitor = new Object();

	private IOException _exceptionWhileSynching;


	public DurableOutputStream(File file) throws IOException {
		_file = file;
		_fileOutputStream = new FileOutputStream(file);
		_fileDescriptor = _fileOutputStream.getFD();
		_objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(_fileOutputStream, 16 * 512)); //Arbitrarily large buffer. Should be a power of two multiplied by 512 bytes (disk sector size).
		startSyncher();
	}


	public void sync(Object object, Turn myTurn) throws IOException {
		SyncBatch myBatch;
		try {
			myTurn.start();
			_objectOutputStream.writeObject(object);
			myBatch = _nextBatchToSync;
		} finally {
			myTurn.end();
		}
		
		myBatch.sync();
		if (_exceptionWhileSynching != null) throw _exceptionWhileSynching;
	}


	private void syncToFile() {
		if (_exceptionWhileSynching != null) return;

		try {
			_objectOutputStream.flush();
			_fileOutputStream.flush();  //ObjectOutputStream > BufferedOutputStream > FileOutputStream. The ObjectOutputStream will flush its underlying stream, but the BufferedOutputStream API documentation (JDK1.4.1_01) does not specify that it must flush its underlying stream too. :P  This line is here in case some obscure implementation doesn't flush the underlying stream.
			
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY); //This line should not be necessary but, with J2RE1.4.0_01 for Windows it actually produces 5x better results (!) for the org.prevayler.demos.scalability transaction execution test.
			_fileDescriptor.sync();
			Thread.currentThread().setPriority(Thread.NORM_PRIORITY); 

		} catch (IOException iox) {
			_exceptionWhileSynching = iox;
		}
	}


	public void close() throws IOException { _objectOutputStream.close(); }


	public File file() { return _file; }


	private void startSyncher() {
		Thread syncher = new Thread() {
			public void run() {
				synchronized (_syncherMonitor) {
					while (true) {
						SyncBatch currentBatch = _nextBatchToSync;
						_nextBatchToSync = new SyncBatch();

						syncToFile();
						currentBatch.setSynched();

						Cool.wait(_syncherMonitor);
					}
				}
			}
		};
		syncher.setDaemon(true);
		syncher.start();
	}


	private class SyncBatch {

		private boolean _isSynching = false;
		private boolean _isSynched = false;

		synchronized void sync() {
			if (_isSynched) return;
			if (!_isSynching) {
				_isSynching = true;
				synchronized (_syncherMonitor) { _syncherMonitor.notify(); }
			}
			Cool.wait(this);
		}

		synchronized void setSynched() {
			_isSynched = true;
			synchronized (this) { notifyAll(); }
		}
	}

}
