//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Carlos Villela.

package org.prevayler.implementation.journal;

import org.prevayler.Transaction;
import org.prevayler.foundation.DurableInputStream;
import org.prevayler.foundation.DurableOutputStream;
import org.prevayler.foundation.FileManager;
import org.prevayler.foundation.StopWatch;
import org.prevayler.foundation.Turn;
import org.prevayler.foundation.monitor.Monitor;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.TransactionTimestamp;
import org.prevayler.implementation.publishing.TransactionSubscriber;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.Date;


/** A Journal that will write all transactions to .journal files.
 */
public class PersistentJournal implements Journal {

	private final FileManager _fileManager;
	private DurableOutputStream _outputJournal;

	private final long _journalSizeThresholdInBytes;
	private final long _journalAgeThresholdInMillis;
	private StopWatch _journalAgeTimer;
	
	private long _nextTransaction;
	private boolean _nextTransactionInitialized = false;
	private Monitor _monitor;

	private final Serializer _journalSerializer;


	/**
	 * @param directory Where transaction journal files will be read and written.
	 * @param journalSizeThresholdInBytes Size of the current journal file beyond which it is closed and a new one started. Zero indicates no size threshold. This is useful journal backup purposes.
	 * @param journalAgeThresholdInMillis Age of the current journal file beyond which it is closed and a new one started. Zero indicates no age threshold. This is useful journal backup purposes.
	 */
	public PersistentJournal(String directory, long journalSizeThresholdInBytes, long journalAgeThresholdInMillis,
							 Serializer journalSerializer, Monitor monitor) throws IOException {
	    _monitor = monitor;
		_fileManager = new FileManager(directory);
		_fileManager.produceDirectory();
		_journalSizeThresholdInBytes = journalSizeThresholdInBytes;
		_journalAgeThresholdInMillis = journalAgeThresholdInMillis;
		_journalSerializer = journalSerializer;
	}


	public void append(Transaction transaction, Date executionTime, Turn myTurn) {
		if (!_nextTransactionInitialized) throw new IllegalStateException("Journal.update() has to be called at least once before Journal.append().");

		DurableOutputStream myOutputJournal;
		DurableOutputStream outputJournalToClose = null;
		
		try {
			myTurn.start();
			if (!isOutputJournalStillValid()) {
				outputJournalToClose = _outputJournal;
				_outputJournal = createOutputJournal(_nextTransaction);
				_journalAgeTimer = StopWatch.start();
			}
			_nextTransaction++;
			myOutputJournal = _outputJournal;
		} finally {
			myTurn.end();
		}

		try {
			myOutputJournal.sync(new TransactionTimestamp(transaction, executionTime), myTurn);
		} catch (IOException iox) {
			handle(iox, _outputJournal.file(), "writing to");
		}

		try {
			myTurn.start();
			try {
				if (outputJournalToClose != null) outputJournalToClose.close();
			} catch (IOException iox) {
				handle(iox, outputJournalToClose.file(), "closing");
			}
		} finally {
			myTurn.end();
		}
		
	}


	private boolean isOutputJournalStillValid() {
		return _outputJournal != null
			&& !isOutputJournalTooBig() 
			&& !isOutputJournalTooOld();
	}


	private boolean isOutputJournalTooOld() {
		return _journalAgeThresholdInMillis != 0
			&& _journalAgeTimer.millisEllapsed() >= _journalAgeThresholdInMillis;
	}


	private boolean isOutputJournalTooBig() {
		return _journalSizeThresholdInBytes != 0
			&& _outputJournal.file().length() >= _journalSizeThresholdInBytes;
	}


	private DurableOutputStream createOutputJournal(long transactionNumber) {
		File file = _fileManager.journalFile(transactionNumber);
		try {
			return new DurableOutputStream(file, _journalSerializer);
		} catch (IOException iox) {
			handle(iox, file, "creating");
			return null;
		}
	}


	/** IMPORTANT: This method cannot be called while the log() method is being called in another thread.
	 * If there are no journal files in the directory (when a snapshot is taken and all journal files are manually deleted, for example), the initialTransaction parameter in the first call to this method will define what the next transaction number will be. We have to find clearer/simpler semantics.
	 */
	public void update(TransactionSubscriber subscriber, long initialTransactionWanted) throws IOException, ClassNotFoundException {
		long initialLogFile = _fileManager.findInitialJournalFile(initialTransactionWanted);
		
		if (initialLogFile == 0) {
			initializeNextTransaction(initialTransactionWanted, 1);
			return;
		}

		long nextTransaction = recoverPendingTransactions(subscriber, initialTransactionWanted, initialLogFile);
		
		initializeNextTransaction(initialTransactionWanted, nextTransaction);
	}


	private void initializeNextTransaction(long initialTransactionWanted, long nextTransaction) throws IOException {
		if (_nextTransactionInitialized) {
			if (_nextTransaction < initialTransactionWanted) throw new IOException("The transaction log has not yet reached transaction " + initialTransactionWanted + ". The last logged transaction was " + (_nextTransaction - 1) + ".");
			if (nextTransaction < _nextTransaction) throw new IOException("Unable to find journal file containing transaction " + nextTransaction + ". Might have been manually deleted.");
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
		File logFile = _fileManager.journalFile(recoveringTransaction);
		DurableInputStream inputLog = new DurableInputStream(logFile, _journalSerializer, _monitor);

		while(true) {
			try {
				if (recoveringTransaction >= initialTransaction) {
					TransactionTimestamp entry = inputLog.read();
					subscriber.receive(entry.transaction(), entry.timestamp());
				} else {
					inputLog.skip();
				}

				recoveringTransaction++;
		
			} catch (EOFException eof) {
				File nextFile = _fileManager.journalFile(recoveringTransaction);
				if (logFile.equals(nextFile)) FileManager.renameUnusedFile(logFile);  //The first transaction in this log file is incomplete. We need to reuse this file name.
				logFile = nextFile;
				if (!logFile.exists()) break;
				inputLog = new DurableInputStream(logFile, _journalSerializer, _monitor);
			}
		}
		return recoveringTransaction;
	}

	protected void handle(IOException iox, File journal, String action) {
		String message = "All transaction processing is now blocked. An IOException was thrown while " + action + " a .journal file.";
	    _monitor.notify(this.getClass(), message, journal, iox);
		hang();
	}

	static private void hang() {
		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException ignored) {
			}
		}
	}


	public void close() throws IOException {
		if (_outputJournal != null) _outputJournal.close();
	}

}
