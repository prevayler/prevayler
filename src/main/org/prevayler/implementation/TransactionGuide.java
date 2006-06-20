// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation;

import org.prevayler.foundation.Chunking;
import org.prevayler.foundation.Guided;
import org.prevayler.foundation.Turn;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class TransactionGuide<X, T> extends Guided {

    private final TransactionTimestamp<X, T> _transactionTimestamp;

    public TransactionGuide(TransactionTimestamp<X, T> transactionTimestamp, Turn pipelineTurn) {
        super(pipelineTurn);
        _transactionTimestamp = transactionTimestamp;
    }

    public TransactionTimestamp<X, T> timestamp() {
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

    @Override public void writeTo(OutputStream stream) throws IOException {
        Chunking.writeChunk(stream, _transactionTimestamp.toChunk());
    }

}
