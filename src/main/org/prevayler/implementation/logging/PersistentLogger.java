// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation.logging;

import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.prevayler.Transaction;
import org.prevayler.foundation.FileManager;
import org.prevayler.foundation.SimpleInputStream;
import org.prevayler.foundation.SimpleOutputStream;
import org.prevayler.implementation.publishing.TransactionSubscriber;


/** A TransactionPublisher that will write all published transactions to .transactionLog files before publishing them to the subscribers.
 */
public class PersistentLogger implements FileFilter, TransactionLogger {

	private final File _directory;
	private long _nextTransaction;
	private SimpleOutputStream _outputLog;


	public PersistentLogger(String directory) throws IOException, ClassNotFoundException {
		_directory = FileManager.produceDirectory(directory);
		File lastFile = lastTransactionFile();
		_nextTransaction = lastFile == null
			? 1
			: number(lastFile) + transactionCount(lastFile);
	}


	public synchronized void log(Transaction transaction, Date executionTime) {
		if (_outputLog == null || !_outputLog.isValid()) createNewOutputLog();
		outputToLog(new TransactionLogEntry(transaction, executionTime));

		_nextTransaction++;
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


	protected void handleExceptionWhileCreating(IOException iox, File logFile) {
		hang(iox, "\nThe exception above was thrown while trying to create file " + logFile + " . Prevayler's default behavior is to display this message and block all transactions. You can change this behavior by extending the PersistentLogger class and overriding the method called: handleExceptionWhileCreating(IOException iox, File logFile).");
	}


	protected void handleExceptionWhileWriting(IOException iox, File logFile, Object objectToWrite, long transactionNumber) {
		hang(iox, "\nThe exception above was thrown while trying to write " + objectToWrite.getClass() + " to file " + logFile + " (Transaction number " + transactionNumber + "). Prevayler's default behavior is to display this message and block all transactions. You can change this behavior by extending the PersistentLogger class and overriding the method called: handleExceptionWhileWriting(IOException iox, File logFile, Object objectToWrite, long transactionNumber).");
	}


	private void createNewOutputLog() {
		File file = transactionLogFile(_nextTransaction);
		try {
			_outputLog = new SimpleOutputStream(file);
		} catch (IOException iox) {
			handleExceptionWhileCreating(iox, file);
		}
	}


	private File lastTransactionFile() throws IOException {
		File[] files = _directory.listFiles(this);
		if (files == null) throw new IOException("Error reading file list from directory " + _directory);
		if (files.length == 0) return null;
		return (File)Collections.max(Arrays.asList(files));
	}


	private void outputToLog(Object object) {
		try {
			_outputLog.writeObject(object);
			_outputLog.sync();
		} catch (IOException iox) {
			handleExceptionWhileWriting(iox, _outputLog.file(), object, _nextTransaction);
		}
	}


	private File transactionLogFile(long transaction) {
		String fileName = "0000000000000000000" + transaction;
		fileName = fileName.substring(fileName.length() - 19) + ".transactionLog";
		return new File(_directory, fileName);
	}


	/** If there are no log files in the directory (when a snapshot is taken and all log files are manually deleted, for example), the initialTransaction parameter in the first call to this method will define what the next transaction number will be. We have to find clearer/simpler semantics.
	 */
	public synchronized void update(TransactionSubscriber subscriber, long initialTransaction) throws IOException, ClassNotFoundException {
		if (initialTransaction > _nextTransaction) throw new IOException("Unable to find transactions from " + _nextTransaction + " to " + (initialTransaction - 1) + ".");
		if (initialTransaction == _nextTransaction) return;

		long initialFileCandidate = initialTransaction;
		while (!transactionLogFile(initialFileCandidate).exists()) {
			initialFileCandidate--;
			if (initialFileCandidate <= 0) throwNotFound(initialTransaction);
		}
		update(subscriber, initialTransaction, initialFileCandidate);
	}


	private void update(TransactionSubscriber subscriber, long initialTransaction, long initialFile) throws IOException, ClassNotFoundException {
		long recoveringTransaction = initialFile;

		SimpleInputStream inputLog = new SimpleInputStream(transactionLogFile(recoveringTransaction));
		while(recoveringTransaction < _nextTransaction) {
			try {
				TransactionLogEntry entry = (TransactionLogEntry)inputLog.readObject();

				if (recoveringTransaction >= initialTransaction)
					subscriber.receive(entry.transaction, entry.timestamp);

				recoveringTransaction++;

			} catch (EOFException eof) {
				File logFile = transactionLogFile(recoveringTransaction);
				if (!logFile.exists()) throwNotFound(recoveringTransaction);
				inputLog = new SimpleInputStream(logFile);
			}
		}
	}

	static private void hang(IOException iox, String message) {
		iox.printStackTrace();
		System.out.println(message);
		while (true) Thread.yield();
	}


	static private long number(File file) {
		return Long.parseLong(file.getName().substring(0, 19));
	}


	static private void throwNotFound(long transaction) throws IOException {
		throw new IOException("Unable to find transactionLog file containing transaction " + transaction);
	}


	/** Returns the number of objects left in the stream and closes it.
	 */
	static private long transactionCount(File logFile) throws IOException, ClassNotFoundException {
		return new SimpleInputStream(logFile).countObjectsLeft();
	}

}
