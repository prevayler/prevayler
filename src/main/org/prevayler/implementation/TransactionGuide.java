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

import org.prevayler.foundation.*;

import java.io.*;
import java.util.*;

public class TransactionGuide<S, R, E extends Exception> extends Guided {

    private final TransactionTimestamp<S, R, E> _transactionTimestamp;

    public TransactionGuide(TransactionTimestamp<S, R, E> transactionTimestamp, Turn pipelineTurn) {
        super(pipelineTurn);
        _transactionTimestamp = transactionTimestamp;
    }

    public TransactionTimestamp<S, R, E> timestamp() {
        return _transactionTimestamp;
    }

    public void checkSystemVersion(long expectedSystemVersion) {
        if (_transactionTimestamp.systemVersion() != expectedSystemVersion) {
            throw new TransactionSequenceError("Attempted to process " + _transactionTimestamp.systemVersion() + " when ready for " + expectedSystemVersion);
        }
    }

    public Date executionTime() {
        return _transactionTimestamp.executionTime();
    }

    @Override public void writeTo(OutputStream stream) throws IOException {
        Chunking.writeChunk(stream, _transactionTimestamp.toChunk());
    }

}
