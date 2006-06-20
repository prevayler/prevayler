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
import org.prevayler.foundation.serialization.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;

public abstract class Capsule<X, T> implements Serializable {

    private final byte[] _serialized;

    protected Capsule(X transaction, Serializer journalSerializer) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            journalSerializer.writeObject(bytes, transaction);
            _serialized = bytes.toByteArray();
        } catch (Exception exception) {
            throw new TransactionNotSerializableError("Unable to serialize transaction", exception);
        }
    }

    protected Capsule(byte[] serialized) {
        _serialized = serialized;
    }

    /**
     * Get the serialized representation of the transaction. Callers must not
     * modify the returned array.
     */
    public byte[] serialized() {
        return _serialized;
    }

    /**
     * Deserialize the contained Transaction or TransactionWithQuery.
     */
    @SuppressWarnings("unchecked") public final X deserialize(Serializer journalSerializer) {
        try {
            return (X) journalSerializer.readObject(new ByteArrayInputStream(_serialized));
        } catch (Exception exception) {
            throw new TransactionNotDeserializableError("Unable to deserialize transaction", exception);
        }
    }

    /**
     * Actually execute the Transaction or TransactionWithQuery. The caller is
     * responsible for synchronizing on the prevalentSystem.
     */
    protected abstract void execute(X transaction, T prevalentSystem, Date executionTime);

    /**
     * Make a clean copy of this capsule that will have its own query result
     * fields.
     */
    public abstract Capsule cleanCopy();

    Chunk toChunk() {
        Chunk chunk = new Chunk(_serialized);
        chunk.setParameter("withQuery", String.valueOf(this instanceof TransactionWithQueryCapsule));
        return chunk;
    }

    static Capsule fromChunk(Chunk chunk) {
        boolean withQuery = Boolean.valueOf(chunk.getParameter("withQuery")).booleanValue();
        if (withQuery) {
            return new TransactionWithQueryCapsule(chunk.getBytes());
        } else {
            return new TransactionCapsule(chunk.getBytes());
        }
    }

}
