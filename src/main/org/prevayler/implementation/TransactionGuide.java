package org.prevayler.implementation;

import org.prevayler.foundation.Chunking;
import org.prevayler.foundation.Guided;
import org.prevayler.foundation.Turn;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class TransactionGuide extends Guided {

	private final TransactionTimestamp _transactionTimestamp;

	public TransactionGuide(TransactionTimestamp transactionTimestamp, Turn pipelineTurn) {
		super(pipelineTurn);
		_transactionTimestamp = transactionTimestamp;
	}

	public TransactionTimestamp timestamp() {
		return _transactionTimestamp;
	}

	public void checkSystemVersion(long expectedSystemVersion) {
		if (_transactionTimestamp.systemVersion() != expectedSystemVersion) {
			throw new IllegalStateException("Attempted to process " + _transactionTimestamp.systemVersion() + " when ready for " + expectedSystemVersion);
		}
	}

	public Date executionTime() {
		return _transactionTimestamp.executionTime();
	}

	public void writeTo(OutputStream stream) throws IOException {
		Chunking.writeChunk(stream, _transactionTimestamp.toChunk());
	}

}
