//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Carlos Villela.

package org.prevayler.implementation.journal;

import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Date;

import org.prevayler.Transaction;
import org.prevayler.foundation.DurableOutputStream;
import org.prevayler.foundation.FileManager;
import org.prevayler.foundation.SimpleInputStream;
import org.prevayler.foundation.StopWatch;
import org.prevayler.foundation.Turn;
import org.prevayler.foundation.monitor.Monitor;
import org.prevayler.implementation.TransactionTimestamp;
import org.prevayler.implementation.publishing.TransactionSubscriber;


/** A Journal that will write all transactions to .journal files.
 */
public class PersistentJournal implements FileFilter, Journal {

	private final File _directory;
	private DurableOutputStream _outputJournal;

	private final long _journalSizeThresholdInBytes;
	private final long _journalAgeThresholdInMillis;
	private StopWatch _journalAgeTimer;
	
	private long _nextTransaction;
	private boolean _nextTransactionInitialized = false;
	private ClassLoader _loader;
	private Monitor _monitor;


	/**
	 * @param directory Where transaction journal files will be read and written.
	 * @param journalSizeThresholdInBytes Size of the current journal file beyond which it is closed and a new one started. Zero indicates no size threshold. This is useful journal backup purposes.
	 * @param journalAgeThresholdInMillis Age of the current journal file beyond which it is closed and a new one started. Zero indicates no age threshold. This is useful journal backup purposes.
	 */
	public PersistentJournal(String directory, long journalSizeThresholdInBytes, long journalAgeThresholdInMillis, ClassLoader loader, Monitor monitor) throws IOException {
	    _monitor = monitor;
		_loader = loader;
		_directory = FileManager.produceDirectory(directory);
		_journalSizeThresholdInBytes = journalSizeThresholdInBytes;
		_journalAgeThresholdInMillis = journalAgeThresholdInMillis;
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
		File file = journalFile(transactionNumber);
		try {
			return new DurableOutputStream(file);
		} catch (IOException iox) {
			handle(iox, file, "creating");
			return null;
		}
	}


	/** IMPORTANT: This method cannot be called while the log() method is being called in another thread.
	 * If there are no journal files in the directory (when a snapshot is taken and all journal files are manually deleted, for example), the initialTransaction parameter in the first call to this method will define what the next transaction number will be. We have to find clearer/simpler semantics.
	 */
	public void update(TransactionSubscriber subscriber, long initialTransactionWanted) throws IOException, ClassNotFoundException {
		long initialLogFile = findInitialJournalFile(initialTransactionWanted);
		
		if (initialLogFile == 0) {
			initializeNextTransaction(initialTransactionWanted, 1);
			return;
		}

		long nextTransaction = recoverPendingTransactions(subscriber, initialTransactionWanted, initialLogFile);
		
		initializeNextTransaction(initialTransactionWanted, nextTransaction);
	}


	private long findInitialJournalFile(long initialTransactionWanted) {
		long initialFileCandidate = initialTransactionWanted;
		while (initialFileCandidate != 0) {   //TODO Optimize.
			if (journalFile(initialFileCandidate).exists()) break;
			initialFileCandidate--;
		}
		return initialFileCandidate;
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
		File logFile = journalFile(recoveringTransaction);
		SimpleInputStream inputLog = new SimpleInputStream(logFile, _loader, _monitor);

		while(true) {
			try {
				TransactionTimestamp entry = (TransactionTimestamp)inputLog.readObject();
		
				if (recoveringTransaction >= initialTransaction)
					subscriber.receive(entry.transaction(), entry.timestamp());
		
				recoveringTransaction++;
		
			} catch (EOFException eof) {
				File nextFile = journalFile(recoveringTransaction);
				if (logFile.equals(nextFile)) renameUnusedFile(logFile);  //The first transaction in this log file is incomplete. We need to reuse this file name.
				logFile = nextFile;
				if (!logFile.exists()) break;
				inputLog = new SimpleInputStream(logFile, _loader, _monitor);
			}
		}
		return recoveringTransaction;
	}


	private void renameUnusedFile(File journalFile) {
		journalFile.renameTo(new File(journalFile.getAbsolutePath() + ".unusedFile" + System.currentTimeMillis()));
	}


	/** Implementing FileFilter. 0000000000000000000.transactionJournal is the format of the transaction journal filename. The long number (19 digits) is the number of the next transaction to be written at the moment the file is created. All transactions written to a file, therefore, have a sequence number greater or equal to the number in its filename.
	 */
	public boolean accept(File file) {
		String name = file.getName();
		if (!name.endsWith(".journal")) return false;
		if (name.length() != 34) return false;
		try { number(file); } catch (RuntimeException r) { return false; }
		return true;
	}

	private File journalFile(long transaction) {
		String fileName = "0000000000000000000" + transaction;
		fileName = fileName.substring(fileName.length() - 19) + ".journal";
		return new File(_directory, fileName);
	}

	static private long number(File file) {
		return Long.parseLong(file.getName().substring(0, 19));
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
