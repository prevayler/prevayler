package org.prevayler.foundation;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for locking files that works for multiple threads. Naive file locking behaves unpredictably if some
 * thread has already acquired a lock on a particular file; attempting another lock may succeed, may fail gracefully, or
 * may fail with an exception. Therefore this class is entirely static and synchronized to provide coordination between
 * all threads in a single JVM.
 */
public class FileLocker {

	private static Map _lockedFiles = new HashMap();

	/**
	 * Attempt to acquire an exclusive lock on the given file. The file, and any parent directories, are created if they do
	 * not exist; but the contents of the file are not harmed if it does exist. If the program needs to actually access the
	 * file, it must use the returned channel exclusively <b>and not close it</b> since that may release the lock. If the
	 * program does not need to access the file, then the returned channel may simply be compared with null and discarded;
	 * it will be closed by {@link #release(File)}.
	 * 
	 * @return A channel for accessing the file if successful; or null if not successful.
	 * @throws OverlappingFileLockException If the JVM detects that another thread already holds a lock on the file. This
	 *                                      should never happen if this method is used exclusively for file locking.
	 */
	public static synchronized FileChannel acquire(File file) throws IOException {
		file = file.getCanonicalFile();

		if (_lockedFiles.containsKey(file)) {
			return null;
		}

		file.getParentFile().mkdirs();
		file.createNewFile();

		RandomAccessFile stream = new RandomAccessFile(file, "rw");
		FileLock lock = stream.getChannel().tryLock();

		if (lock == null) {
			stream.close();
			return null;
		}

		_lockedFiles.put(file, lock);
		return stream.getChannel();
	}

	/**
	 * Release a file lock and close the associated channel. May be called only once after a successful call to {@link
	 * #acquire(File)} on the same file.
	 */
	public static synchronized void release(File file) throws IOException {
		FileLock lock = (FileLock) _lockedFiles.remove(file.getCanonicalFile());
		lock.release();
		lock.channel().close();
	}

}
