//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import org.prevayler.foundation.Chunk;

import java.io.Serializable;
import java.util.Date;

public class TransactionTimestamp implements Serializable {

	static final long serialVersionUID = 1L;

	private final Capsule _capsule;
	private final long _systemVersion;
	private final long _executionTime;

	public TransactionTimestamp(Capsule capsule, long systemVersion, Date executionTime) {
		this(capsule, systemVersion, executionTime.getTime());
	}

	private TransactionTimestamp(Capsule capsule, long systemVersion, long executionTime) {
		_capsule = capsule;
		_systemVersion = systemVersion;
		_executionTime = executionTime;
	}

	public Capsule capsule() {
		return _capsule;
	}

	public long systemVersion() {
		return _systemVersion;
	}

	public Date executionTime() {
		return new Date(_executionTime);
	}

	public TransactionTimestamp cleanCopy() {
		return new TransactionTimestamp(_capsule.cleanCopy(), _systemVersion, _executionTime);
	}

	public Chunk toChunk() {
		Chunk chunk = _capsule.toChunk();
		chunk.setParameter("systemVersion", String.valueOf(_systemVersion));
		chunk.setParameter("executionTime", String.valueOf(_executionTime));
		return chunk;
	}

	public static TransactionTimestamp fromChunk(Chunk chunk) {
		Capsule capsule = Capsule.fromChunk(chunk);
		long systemVersion = Long.parseLong(chunk.getParameter("systemVersion"));
		long executionTime = Long.parseLong(chunk.getParameter("executionTime"));
		return new TransactionTimestamp(capsule, systemVersion, executionTime);
	}

}
