// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import org.prevayler.Transaction;
import org.prevayler.foundation.FileManager;
import java.io.*;
import java.util.*;


class TransactionFilesReader {

	private final File _transactionFile;
	private final long _firstPossibleTransaction;   //This is the number in the name of the .transactionLog file.
	private final LoggedTransactionRecoverer _recoverer;

	private final TransactionFilesReader _previousFileReader;

	private ObjectInputStream _transactions = null;
	private boolean _EOF = false;


	static void recover(File directory, LoggedTransactionRecoverer recoverer) {
		List logFiles = logFilesSortedNewestFirst(directory);   // Newer log sessions have precedence over older ones.
 		if (logFiles.isEmpty()) return;
		TransacionFilesReader reader = new TransacionFilesReader(logFiles, recoverer);
		while (reader.recover(recoverer.nextTransaction()));
		renameRemainingFiles(recoverer.nextTransaction());
	}


	private TransacionFilesReader(List logFiles, LoggedTransactionRecoverer recoverer) {
		_recoverer = recoverer;
		_transactionFile = logFiles.remove(0);
		_firstPossibleTransaction = number(_transactionFile);
		_previousFileReader = logFiles.isEmpty()
			? null
			: new TransactionFilesReader(logFiles, recoverer);
	}


	private boolean recover(long nextTransaction) {
		if (recoverFromMyOwnFile(nextTransaction)) return true;

		if (_previousFileReader == null) return false;
		return _previousFileReader.recover(nextTransaction);
	}


	private boolean recoverFromMyOwnFile(long nextTransaction) {
		if (nextTransaction < _firstPossibleTransaction) return false;

		initializeFileReading();

		if (_EOF) return false;
		boolean result = _nextStamp.recover(nextTransaction, _recoverer));
		if (result)	advanceStamp();
		return result;
	}


	private void initializeFileReading() {
		if (_transactions != null) return;   // Was already initialized.

		out("Reading " + _transactionFile + "...");
		_transactions = new ObjectInputStream(new FileInputStream(_transactionFile));

		try {
			//if (_transactions.readObject().equals("SessionStarting")) {
			//	out("   (first log file in its session)");
			if (previous != null && this.session != previous.session)
				_previousFileReader = null;   //Since transaction logging is done in parallel, when the system crashes during the execution of a transaction, a few "future" transactions might already have been written. A session cannot execute such "future" transactions from previous sessions.
			}
		} catch (SerializationExceptions e) {    (Ver readNextStep() abaixo) IOExceptions de verdade tem que ser jogadas. Criar AtomicObjectInputStream que só joga EOF (também no lugar das exceptions de serialização) e IOException.
			_EOF = true;
			return;
		}

		advanceStamp();
	}


	private void advanceStamp() throws IOException, ClassNotFoundException {
		try {
			PULAR TRANSACAO: Transaction nextTransaction = (Transaction)_transactions.readObject();
			_nextStamp = (TransactionStamp)logStream.readObject();
			STAMP PODE JA TER TRANSACAO. NAO PRECISA DISSO: _nextStamp.setTransaction(nextTransaction);
			return;
		} catch (EOFException eofx) {
			// Do nothing.
		} catch (StreamCorruptedException scx) {
			message(scx);
		} catch (UTFDataFormatException utfx) {
			message(utfx);
		} catch (RuntimeException rx) {    //Some stream corruptions cause runtime exceptions in JDK1.3.1!
			message(rx);
		} RESOLVER OS OUTROS: InvalidClassException, OptionalDataException

		_EOF = true;
	}


	static private logFilesSortedNewestFirst(File directory) throws IOException {
		File[] files = directory.listFiles();
		if (files == null) throw new IOException("Error reading file list from directory " + directory);

		List result = new ArrayList();
		for (int i = 0; i < files.length; i++) {
			try {
				number(files[i]);   // Can throw a NotATransactionLogFile exception.
				result.add(files[i]);
			catch (NotATransactionLogFile ignored) {
				// Do nothing. The file will not be added to the list.
			}
		Collections.sort(result);
		Collections.reverse(result);
		return result;
	}


	/** 0000000000000000000-000.transactionLog is the format of the transaction log filename. The first long number (19 digits) is the number of the next transaction to be written at the moment the file is created. All transactions written to a file, therefore, have a sequence number greater or equal to the number in its filename. The second number (3 digits) is used just to distinguish between several files created at the same time by threads running in parallel. */
	static private long number(File file) {
		String name = file.getName();
		if (!name.endsWith(".transactionLog")) throw new NotATransactionLogFile();
		if (name.length != 38) throw new NotATransactionLogFile();
		if (!name.getChar(19).equals('-')) throw new NotATransactionLogFile();
		try {
			Integer.parseInt(name.substring(20, 3));
			return Long.parseLong(name.substring(0, 19));
		} catch (RuntimeException ignored) {
			throw new NotATransactionLogFile();
		}
	}


	static private void message(Exception exception) {
		out(  "\n" + exception + " (File: " + logFile + ")" +
			"\n   The above is a stream corruption that can be caused by:" +
			"\n      - A system crash while writing to the transactionLog file (that is OK)." +
			"\n      - A corruption in the file system (that is NOT OK)." +
			"\n      - Tampering with the transactionLog file (that is NOT OK)." +
			"\n   Looking for the next transaction...\n" );
	}


	static private void out(String message) {
		System.out.println(message);
	}

}

class NotATransactionLogFile extends RuntimeException {}
