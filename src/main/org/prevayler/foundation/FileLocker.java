package org.prevayler.foundation;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

/**
 * A utility class for locking files that works for multiple threads. Naive file locking behaves unpredictably if some
 * thread has already acquired a lock on a particular file; attempting another lock may succeed, may fail gracefully, or
 * may fail with an exception. Therefore this class is entirely static and synchronized to provide coordination between
 * all threads in a single JVM.
 */
public class FileLocker {

	private File _file;
	private RandomAccessFile _stream;
	private FileLock _lock;

	/**
	 * Attempt to acquire an exclusive lock on the given file. The file, and any parent directories, are created if they do
	 * not exist; but the contents of the file are not harmed if it does exist. If the program needs to actually access the
	 * file, it must use {@link #getStream()} exclusively <b>and not close it</b> since that may release the lock.
	 * 
	 * @throws IOException If the lock cannot be acquired.
	 * @throws java.nio.channels.OverlappingFileLockException
	 *                     If the JVM detects that another thread already holds a lock on the file. This should never
	 *                     happen if this method is used exclusively for file locking.
	 */
	public FileLocker(File file) throws IOException {
		_file = file.getCanonicalFile();

		// Test-and-set JVM-global property; setProperty is atomic.
		if ("locked".equals(System.setProperty(propertyName(), "locked"))) {
			throw new IOException("Already locked internally");
		}

		_file.getParentFile().mkdirs();
		_file.createNewFile();

		_stream = new RandomAccessFile(_file, "rw");

		try {
			_lock = _stream.getChannel().tryLock();
		} catch (IOException e) {
			_stream.close();
			throw e;
		}

		if (_lock == null) {
			_stream.close();
			System.setProperty(propertyName(), "");
			throw new IOException("Already locked externally");
		}
	}

	private String propertyName() throws IOException {
		return FileLocker.class.getName() + "-" + _file.getCanonicalPath();
	}

	/**
	 * Release the file lock and close the associated stream.
	 */
	public void release() throws IOException {
		try {
			try {
				_lock.release();
			} finally {
				_stream.close();
			}
		} finally {
			System.setProperty(propertyName(), "");
		}
	}

	public RandomAccessFile getStream() {
		return _stream;
	}

}
