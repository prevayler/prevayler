// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import org.prevayler.Transaction;
import org.prevayler.foundation.FileManager;
import java.io.*;
import java.util.*;

/** Logs and recovers transactions to/from files.
*/
public class TransactionLogManager {
	
	private final File _transactionLogDirectory;
	private final int _transactionLogs;

	private final List _availableLoggers = new LinkedList();
	private boolean _loggersCreated = false;
	
	/**
	 * Creates a TransactionlogManager that will write to 5 transactionLogFiles
	 * in parallel in the prevalanceBase directory.
	 * @param transactionLogDirectoryName The path of the directory where the
	 * pending log files will be read and where the new log files will be
	 * created.
	 */
	public TransactionLogManager() throws IOException {
		this("PrevalenceBase");
	}

	/**
	 * Creates a TransactionlogManager that will write to 5 transactionLogFiles
	 * in parallel.
	 * @param transactionLogDirectoryName The path of the directory where the pending log files will be read and where the new log files will be created.
	 */
	public TransactionLogManager(String transactionLogDirectoryName) throws IOException {
		this(transactionLogDirectoryName, 5);
	}

	/** @param transactionLogDirectoryName The path of the directory where the pending log files will be read and where the new log files will be created.
	* @param transactionLogs The number of simultaneous transactionLog files
	* that will be created for parallel transaction logging.
	*/
	public TransactionLogManager(String transactionLogDirectoryName, int transactionLogs) throws IOException {
		_transactionLogDirectory = FileManager.produceDirectory(transactionLogDirectoryName);
		_transactionLogs = transactionLogs;
	}

	Transaction recover(long l) {
		return null;
	}



	TransactionLogger availableTransactionLogger() throws IOException {
		synchronized (_availableLoggers) {
			while (_availableLoggers.isEmpty()) {
					if (_loggersCreated) {
						waitForAvailableLogger();
					} else {
						createLoggers();
					}
			}

			return (TransactionLogger)_availableLoggers.remove(0);
		}
	}

	private void waitForAvailableLogger() {
		try {
			_availableLoggers.wait();
		} catch (InterruptedException ix) {
			throw new RuntimeException("Unexpected InterruptedException.");
		}
	}

	private void createLoggers() throws IOException {
		createLogger(_transactionLogDirectory, true);
		for (int i = 2; i <= _transactionLogs; i++) {
			createLogger(_transactionLogDirectory, false);
		}

		_loggersCreated = true;
	}

	private void createLogger(File directory, boolean sequenceRestarted) throws IOException {
		File logFile = new File(directory, logFileName(444));
		if(!logFile.createNewFile()) throw new IOException("Attempt to create transaction log file that already existed: " + logFile);;
		
		
		_availableLoggers.add(new TransactionLogger(logFile, sequenceRestarted));
	}


	static String logFileName(long fileNumber) {
		String fileName = "000000000000000000000" + fileNumber;
		return fileName.substring(fileName.length() - 21) + ".transactionLog";
	}


	void flushToDisk(TransactionLogger transactionLogger) throws IOException {
		transactionLogger.flushToDisk();
		makeLoggerAvailable(transactionLogger);
	}

	private void makeLoggerAvailable(TransactionLogger transactionLogger) throws IOException {
		synchronized (_availableLoggers) {
			if (transactionLogger.isValid()) {
				_availableLoggers.add(transactionLogger);
			} else {
				transactionLogger.close();
				createLogger(transactionLogger.directory(),false);
			}

			_availableLoggers.notify();
		}
	}

}
