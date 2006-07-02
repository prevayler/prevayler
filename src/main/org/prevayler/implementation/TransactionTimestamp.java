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

import org.prevayler.foundation.Chunk;

import java.io.Serializable;
import java.util.Date;

public class TransactionTimestamp<S, R, E extends Exception> implements Serializable {

    static final long serialVersionUID = 1L;

    private final TransactionCapsule<S, R, E> _capsule;

    private final long _systemVersion;

    private final long _executionTime;

    public TransactionTimestamp(TransactionCapsule<S, R, E> capsule, long systemVersion, Date executionTime) {
        this(capsule, systemVersion, executionTime.getTime());
    }

    private TransactionTimestamp(TransactionCapsule<S, R, E> capsule, long systemVersion, long executionTime) {
        _capsule = capsule;
        _systemVersion = systemVersion;
        _executionTime = executionTime;
    }

    public TransactionCapsule<S, R, E> capsule() {
        return _capsule;
    }

    public long systemVersion() {
        return _systemVersion;
    }

    public Date executionTime() {
        return new Date(_executionTime);
    }

    public TransactionTimestamp<S, R, E> cleanCopy() {
        return new TransactionTimestamp<S, R, E>(_capsule.cleanCopy(), _systemVersion, _executionTime);
    }

    public Chunk toChunk() {
        Chunk chunk = _capsule.toChunk();
        chunk.setParameter("systemVersion", String.valueOf(_systemVersion));
        chunk.setParameter("executionTime", String.valueOf(_executionTime));
        return chunk;
    }

    @SuppressWarnings("unchecked") public static <S> TransactionTimestamp<S, ?, ?> fromChunk(Chunk chunk) {
        TransactionCapsule<S, ?, ?> capsule = TransactionCapsule.fromChunk(chunk);
        long systemVersion = Long.parseLong(chunk.getParameter("systemVersion"));
        long executionTime = Long.parseLong(chunk.getParameter("executionTime"));
        return new TransactionTimestamp(capsule, systemVersion, executionTime);
    }

}
