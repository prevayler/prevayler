//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation.logging;

import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Date;

import org.prevayler.Transaction;
import org.prevayler.foundation.FileManager;
import org.prevayler.foundation.SimpleInputStream;
import org.prevayler.foundation.DurableOutputStream;
import org.prevayler.foundation.StopWatch;
import org.prevayler.foundation.Turn;
import org.prevayler.implementation.*;
import org.prevayler.implementation.publishing.TransactionSubscriber;


/** A TransactionLogger that will write all transactions to .transactionLog files.
 */
public class PersistentLogger implements FileFilter, TransactionLogger {

	private final File _directory;
	private DurableOutputStream _outputLog;

	private final long _logSizeThresholdInBytes;
	private final long _logAgeThresholdInMillis;
	private StopWatch _logAgeTimer;
	
	private long _nextTransaction;
	private final Object _nextTransactionMonitor = new Object();
	private boolean _nextTransactionInitialized = false;


	/**
	 * @param directory Where transactionLog files will be read and written.
	 * @param logSizeThresholdInBytes Size of the current transactionLog file beyond which it is closed and a new one started. Zero indicates no size threshold. This is useful transactionLog backup purposes.
	 * @param logAgeThresholdInMillis Age of the current transactionLog file beyond which it is closed and a new one started. Zero indicates no age threshold. This is useful transactionLog backup purposes.
	 */
	public PersistentLogger(String directory, long logSizeThresholdInBytes, long logAgeThresholdInMillis) throws IOException {
		_directory = FileManager.produceDirectory(directory);
		_logSizeThresholdInBytes = logSizeThresholdInBytes;
		_logAgeThresholdInMillis = logAgeThresholdInMillis;
	}


	public void log(Transaction transaction, Date executionTime, Turn myTurn) {
		if (!_nextTransactionInitialized) throw new IllegalStateException("TransactionLogger.update() has to be called at least once before TransactionLogger.log().");

		prepareOutputLog();
		try {
			_outputLog.sync(new TransactionTimestamp(transaction, executionTime), myTurn);
		} catch (IOException iox) {
			handleExceptionWhileWriting(iox, _outputLog.file());
		}
	}


	private void prepareOutputLog() {
		synchronized (_nextTransactionMonitor) {
			if (!isOutputLogValid()) createNewOutputLog(_nextTransaction);   //TODO Create new output log when size threshold surpassed or age expires.
			_nextTransaction++;  //The transaction count is increased but, because of thread concurrency, it is not guaranteed that this transaction is the _nextTransaction'th transaction, so don't trust that. It is myTurn that will guarantee execution in the correct order.
		}
	}


	private boolean isOutputLogValid() {
		return _outputLog != null
			&& !isOutputLogTooBig() 
			&& !isOutputLogTooOld();
	}


	private boolean isOutputLogTooOld() {
		return _logAgeThresholdInMillis != 0
			&& _logAgeTimer.millisEllapsed() >= _logAgeThresholdInMillis;
	}


	private boolean isOutputLogTooBig() {
		return _logSizeThresholdInBytes != 0
			&& _outputLog.file().length() >= _logSizeThresholdInBytes;
	}


	private void createNewOutputLog(long transactionNumber) {
		File file = transactionLogFile(transactionNumber);
		try {
			if (_outputLog != null) _outputLog.close();
			_outputLog = new DurableOutputStream(file);
			_logAgeTimer = StopWatch.start();
		} catch (IOException iox) {
			handleExceptionWhileCreating(iox, file);
		}
	}


	/** IMPORTANT: This method cannot be called while the log() method is being called in another thread.
	 * If there are no log files in the directory (when a snapshot is taken and all log files are manually deleted, for example), the initialTransaction parameter in the first call to this method will define what the next transaction number will be. We have to find clearer/simpler semantics.
	 */
	public void update(TransactionSubscriber subscriber, long initialTransactionWanted) throws IOException, ClassNotFoundException {
		long initialLogFile = findInitialLogFile(initialTransactionWanted);
		
		if (initialLogFile == 0) {
			initializeNextTransaction(initialTransactionWanted, 1);
			return;
		}

		long nextTransaction = recoverPendingTransactions(subscriber, initialTransactionWanted, initialLogFile);
		
		initializeNextTransaction(initialTransactionWanted, nextTransaction);
	}


	private long findInitialLogFile(long initialTransactionWanted) {
		long initialFileCandidate = initialTransactionWanted;
		while (initialFileCandidate != 0) {   //TODO Optimize.
			if (transactionLogFile(initialFileCandidate).exists()) break;
			initialFileCandidate--;
		}
		return initialFileCandidate;
	}


	private void initializeNextTransaction(long initialTransactionWanted, long nextTransaction) throws IOException {
		if (_nextTransactionInitialized) {
			if (_nextTransaction < initialTransactionWanted) throw new IOException("The transaction log has not yet reached transaction " + initialTransactionWanted + ". The last logged transaction was " + (_nextTransaction - 1) + ".");
			if (nextTransaction < _nextTransaction) throw new IOException("Unable to find transactionLog file containing transaction " + nextTransaction + ". Might have been manually deleted.");
			if (nextTransaction > _nextTransaction) throw new IllegalStateException();
			return;
		}
		_nextTransactionInitialized = true;
		_nextTransaction = initialTransactionWanted > nextTransaction
			? initialTransactionWanted
			: nextTransaction;
	}


	private long recoverPendingTransactions(TransactionSubscriber subscriber, long initialTransaction, long initialLogFile)	throws IOException, ClassNotFoundException {
		long recoveringTransaction = initialLogFile;
		File logFile = transactionLogFile(recoveringTransaction);
		SimpleInputStream inputLog = new SimpleInputStream(logFile);

		while(true) {
			try {
				TransactionTimestamp entry = (TransactionTimestamp)inputLog.readObject();
		
				if (recoveringTransaction >= initialTransaction)
					subscriber.receive(entry.transaction(), entry.timestamp());
		
				recoveringTransaction++;
		
			} catch (EOFException eof) {
				File nextFile = transactionLogFile(recoveringTransaction);
				if (logFile.equals(nextFile)) renameUnusedFile(logFile);  //The first transaction in this log file is incomplete. We need to reuse this file name.
				logFile = nextFile;
				if (!logFile.exists()) break;
				inputLog = new SimpleInputStream(logFile);
			}
		}
		return recoveringTransaction;
	}


	private void renameUnusedFile(File logFile) {
		logFile.renameTo(new File(logFile.getAbsolutePath() + ".unusedFile" + System.currentTimeMillis()));
	}


	/** Implementing FileFilter. 0000000000000000000.transactionLog is the format of the transaction log filename. The long number (19 digits) is the number of the next transaction to be written at the moment the file is created. All transactions written to a file, therefore, have a sequence number greater or equal to the number in its filename.
	 */
	public boolean accept(File file) {
		String name = file.getName();
		if (!name.endsWith(".transactionLog")) return false;
		if (name.length() != 34) return false;
		try { number(file); } catch (RuntimeException r) { return false; }
		return true;
	}

	private File transactionLogFile(long transaction) {
		String fileName = "0000000000000000000" + transaction;
		fileName = fileName.substring(fileName.length() - 19) + ".transactionLog";
		return new File(_directory, fileName);
	}

	static private long number(File file) {
		return Long.parseLong(file.getName().substring(0, 19));
	}


	protected void handleExceptionWhileCreating(IOException iox, File logFile) {
		hang(iox, "\nThe exception above was thrown while trying to create file " + logFile + " . Prevayler's default behavior is to display this message and block all transactions. You can change this behavior by extending the PersistentLogger class and overriding the method called: handleExceptionWhileCreating(IOException iox, File logFile).");
	}


	protected void handleExceptionWhileWriting(IOException iox, File logFile) {
		hang(iox, "\nThe exception above was thrown while trying to write to file " + logFile + " . Prevayler's default behavior is to display this message and block all transactions. You can change this behavior by extending the PersistentLogger class and overriding the method called: handleExceptionWhileWriting(IOException iox, File logFile).");
	}


	static private void hang(IOException iox, String message) {
		iox.printStackTrace();
		System.out.println(message);
		while (true) Thread.yield();
	}


	public void close() throws IOException {
		if (_outputLog != null) _outputLog.close();
	}

}
