//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2005 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Justin Sampson, Tobias Hill.

package org.prevayler.foundation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

public class DurableOutputStream {
	/**
	 * These two locks allow the two main activities of this class,
	 * serializing transactions to a buffer on the one hand and flushing
	 * the buffer and syncing to disk on the other hand, to proceed
	 * concurrently. Note that where both locks are required, we always
	 * acquire the _syncLock before acquiring the _writeLock to avoid
	 * deadlock.
	 */
	private final Object _writeLock = new Object();
	private final Object _syncLock = new Object();

	/** The File object is only stashed for the sake of the file() getter. */
	private final File _file;

	/** All access guarded by _syncLock. */
	private final FileOutputStream _fileOutputStream;

	/** All access guarded by _syncLock. */
	private final FileDescriptor _fileDescriptor;

	/** All access guarded by _writeLock. */
	private ByteArrayOutputStream _active = new ByteArrayOutputStream();

	/** All access guarded by _syncLock. */
	private ByteArrayOutputStream _inactive = new ByteArrayOutputStream();

	/** All access guarded by _writeLock. */
	private boolean _closed = false;

	/** All access guarded by _writeLock. */
	private int _objectsWritten = 0;

	/** All access guarded by _syncLock. */
	private int _objectsSynced = 0;

	/** All access guarded by _syncLock. */
	private int _fileSyncCount = 0;

	public DurableOutputStream(File file) throws IOException {
		_file = file;
		_fileOutputStream = new FileOutputStream(file);
		_fileDescriptor = _fileOutputStream.getFD();
	}

	public void sync(Guided guide) throws IOException {
		int thisWrite;

		// When a thread arrives here, all we care about at first is that it
		// gets properly sequenced according to its turn.

		guide.startTurn();
		try {
			thisWrite = writeObject(guide);
		} finally {
			guide.endTurn();
		}

		// Now, having ended the turn, the next thread is allowed to come in
		// and try to write its object before we get to the sync.

		waitUntilSynced(thisWrite);
	}

	private int writeObject(Guided guide) throws IOException {
		synchronized (_writeLock) {
			if (_closed) {
				throw new IOException("already closed");
			}

			try {
				guide.writeTo(_active);
			} catch (IOException exception) {
				internalClose();
				throw exception;
			}

			_objectsWritten++;
			return _objectsWritten;
		}
	}

	private void waitUntilSynced(int thisWrite) throws IOException {
		// Here's the real magic. If this thread is the first to have written
		// an object after a period of inactivity, and there are no other
		// threads coming in, then thisWrite is trivially one greater than
		// _objectsSynced, so this thread goes right ahead to sync its own
		// object alone. But then, if another thread comes along and writes
		// another object while this thread is syncing, it will write to the
		// _active buffer and then promply block on the _syncLock until this
		// thread finishes the sync. If threads continue to come in at just
		// about the rate that syncs can happen, each thread will wait for the
		// previous sync to complete and then initiate its own sync. The
		// latency for the first thread is exactly the time for one sync, which
		// is the minimum possible latency; the latency for any later thread is
		// somewhere between that minimum and a maximum of two syncs, with the
		// average being closer to the minimum end.
		//
		// Now, consider the steady state under heavy load. Some thread will
		// always be syncing the _inactive buffer to disk, so every thread that
		// arrives will write its object to the _active buffer and then wait
		// here on the _syncLock. If 10 threads arrive during a given sync
		// operation, then _active will hold 10 objects when that sync
		// completes. As soon as that earlier thread releases _syncLock, one of
		// those 10 new threads will acquire the lock and notice that its
		// object has not yet been synced; it will then swap the buffers and
		// flush and sync all 10 objects at once. Each of the 10 threads will
		// acquire _syncLock in turn and now see that their object has already
		// been synced and do nothing.

		synchronized (_syncLock) {
			if (_objectsSynced < thisWrite) {
				int objectsWritten;
				synchronized (_writeLock) {
					if (_closed) {
						throw new IOException("already closed");
					}

					ByteArrayOutputStream swap = _active;
					_active = _inactive;
					_inactive = swap;

					objectsWritten = _objectsWritten;
				}

				try {
					// Resetting the buffer clears its contents but keeps the
					// allocated space. Therefore the buffers should quickly
					// reach a steady state of an appropriate size and then not
					// need to grow any more.

					_inactive.writeTo(_fileOutputStream);
					_inactive.reset();
					_fileOutputStream.flush();

					_fileDescriptor.sync();

				} catch (IOException exception) {
					internalClose();
					throw exception;
				}

				_objectsSynced = objectsWritten;
				_fileSyncCount++;
			}
		}
	}

	public void close() throws IOException {
		synchronized (_syncLock) {
			synchronized (_writeLock) {
				if (_closed) {
					return;
				}

				internalClose();
				_fileOutputStream.close();
			}
		}
	}

	private void internalClose() {
		synchronized (_writeLock) {
			_closed = true;
			_active = null;
			_inactive = null;
		}
	}

	public File file() {
		return _file;
	}

	public synchronized int fileSyncCount() {
		synchronized (_syncLock) {
			return _fileSyncCount;
		}
	}

	public boolean reallyClosed() {
		synchronized (_writeLock) {
			return _closed;
		}
	}
}
