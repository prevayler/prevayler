package org.prevayler.implementation;

import org.prevayler.foundation.Turn;

import java.util.Date;

public class TransactionGuide {

	private final TransactionTimestamp _transactionTimestamp;
	private final Turn _pipelineTurn;

	public TransactionGuide(TransactionTimestamp transactionTimestamp, Turn pipelineTurn) {
		_transactionTimestamp = transactionTimestamp;
		_pipelineTurn = pipelineTurn;
	}

	public TransactionTimestamp timestamp() {
		return _transactionTimestamp;
	}

	public void startTurn() {
		_pipelineTurn.start();
	}

	public void endTurn() {
		_pipelineTurn.end();
	}

	public void checkSystemVersion(long expectedSystemVersion) {
		if (_transactionTimestamp.systemVersion() != expectedSystemVersion) {
			throw new IllegalStateException("Attempted to process " + _transactionTimestamp.systemVersion() + " when ready for " + expectedSystemVersion);
		}
	}

	public Date executionTime() {
		return _transactionTimestamp.executionTime();
	}

}
