// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation.log;

import java.io.*;
import java.util.*;
import org.prevayler.foundation.*;
import org.prevayler.implementation.*;
import org.prevayler.Transaction;


/** A TransactionPublisher that will write all published transactions to .transactionLog files before publishing them to the subscribers.
 */
public class TransactionLogger extends TransientPublisher implements FileFilter {

	private final File _directory;

	private boolean _nextTransactionKnown = false;
	private long _nextTransaction;
	private SimpleOutputStream _outputLog;


	public TransactionLogger(String directory) throws IOException, ClassNotFoundException {
		_directory = FileManager.produceDirectory(directory);
		File lastFile = lastTransactionFile();
		if (lastFile != null) {
			_nextTransaction = number(lastFile) + new SimpleInputStream(lastFile).objectCount();
			_nextTransactionKnown = true;
		}
	}


	public synchronized void publish(Transaction transaction) {
		if (!_nextTransactionKnown) throw new RuntimeException("The sequence number for the next transaction to be logged is undefined. This happens when there are no transactionLog files in the directory and publish() is called before a TransactionSubscriber has been added.");
		if (_outputLog == null || !_outputLog.isValid()) createNewOutputLog();

		try {
			_outputLog.writeObject(transaction);
			_outputLog.sync();
		} catch (IOException iox) {
			handleExceptionWhileWriting(iox, _outputLog.file(), transaction, _nextTransaction);
		}

		_nextTransaction++;
		super.publish(transaction);
	}


	/** If there are no log files in the directory (when a snapshot is taken and all log files are manually deleted, for example), the initialTransaction parameter in the first call to this method, will define what the next transaction number will be. We have to find clearer/simpler semantics.
	 */
	public synchronized void addSubscriber(TransactionSubscriber subscriber, long initialTransaction) throws IOException, ClassNotFoundException {
		if (!_nextTransactionKnown) {
			_nextTransaction = initialTransaction;
			_nextTransactionKnown = true;
		} else {
			if (initialTransaction > _nextTransaction) throw new IOException("Unable to find transactions from " + _nextTransaction + " to " + (initialTransaction - 1) + ".");
			long initialFileCandidate = initialTransaction;
			while (!transactionLogFile(initialFileCandidate).exists()) {
				initialFileCandidate--;
				if (initialFileCandidate <= 0) throwNotFound(initialTransaction);
			}
			update(subscriber, initialTransaction, initialFileCandidate);
		}
		super.addSubscriber(subscriber, -1);
	}


	private File lastTransactionFile() throws IOException {		
		File[] files = _directory.listFiles(this);
		if (files == null) throw new IOException("Error reading file list from directory " + _directory);
		if (files.length == 0) return null;
		return (File)Collections.max(Arrays.asList(files));
	}


	/** Implementing FileFilter.
	 */
	public boolean accept(File file) {
		String name = file.getName();
		if (!name.endsWith(".transactionLog")) return false;
		if (name.length() != 34) return false;
		try {	number(file); } catch (RuntimeException r) { return false; }
		return true;
	}


	/** 0000000000000000000.transactionLog is the format of the transaction log filename. The long number (19 digits) is the number of the next transaction to be written at the moment the file is created. All transactions written to a file, therefore, have a sequence number greater or equal to the number in its filename.
	 */
	static private long number(File file) {
		return Long.parseLong(file.getName().substring(0, 19));
	}


	private void createNewOutputLog() {
		File file = transactionLogFile(_nextTransaction);
		try {
			_outputLog = new SimpleOutputStream(file);
		} catch (IOException iox) {
			handleExceptionWhileCreating(iox, file);
		}
	}


	private File transactionLogFile(long transaction) {
		String fileName = "0000000000000000000" + transaction;
		fileName = fileName.substring(fileName.length() - 19) + ".transactionLog";
		return new File(_directory, fileName);
	}


	private void update(TransactionSubscriber subscriber, long initialTransaction, long initialFile) throws IOException, ClassNotFoundException {
		long recoveringTransaction = initialFile;
		SimpleInputStream inputLog = new SimpleInputStream(transactionLogFile(recoveringTransaction));
		while(recoveringTransaction < _nextTransaction) {
			try {
				Transaction transaction = (Transaction)inputLog.readObject();
				if (recoveringTransaction >= initialTransaction) subscriber.receive(transaction);
				recoveringTransaction++;
			} catch (EOFException eof) {
				File logFile = transactionLogFile(recoveringTransaction);
				if (!logFile.exists()) throwNotFound(recoveringTransaction);
				inputLog = new SimpleInputStream(logFile);
			}
		}
	}


	static private void throwNotFound(long transaction) throws IOException {
		throw new IOException("Unable to find transactionLog file containing transaction " + transaction);
	}


	protected void handleExceptionWhileWriting(IOException iox, File logFile, Transaction transaction, long transactionNumber) {
		hang(iox, "\nThe exception above was thrown while trying to write transaction " + transactionNumber + " to file " + logFile + " . Prevayler's default behavior is to display this message and stop all transaction logging. You can change this behavior by extending the TransactionLogger class and overriding the method called: handleExceptionWhileWriting(IOException iox, File logFile, Transaction transaction, long transactionNumber).");
	}


	protected void handleExceptionWhileCreating(IOException iox, File logFile) {
		hang(iox, "\nThe exception above was thrown while trying to create file " + logFile + " . Prevayler's default behavior is to display this message and stop all transaction logging. You can change this behavior by extending the TransactionLogger class and overriding the method called: handleExceptionWhileCreating(IOException iox, File logFile).");
	}


	static private void hang(IOException iox, String message) {
		iox.printStackTrace();
		System.out.println(message);
		while (true) Thread.yield();
	}

}
