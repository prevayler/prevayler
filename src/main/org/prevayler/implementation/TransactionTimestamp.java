//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import org.prevayler.foundation.serialization.Serializer;

import java.io.Serializable;
import java.util.Date;

public class TransactionTimestamp implements Serializable {

	static final long serialVersionUID = 1L;

	private final TransactionCapsule _transactionCapsule;
	private final long _systemVersion;
	private final long _executionTime;

	public TransactionTimestamp(TransactionCapsule transactionCapsule, long systemVersion, Date executionTime) {
		this(transactionCapsule, systemVersion, executionTime.getTime());
	}

	private TransactionTimestamp(TransactionCapsule transactionCapsule, long systemVersion, long executionTime) {
		_transactionCapsule = transactionCapsule;
		_systemVersion = systemVersion;
		_executionTime = executionTime;
	}

	public TransactionCapsule capsule() {
		return _transactionCapsule;
	}

	public long systemVersion() {
		return _systemVersion;
	}

	public Date executionTime() {
		return new Date(_executionTime);
	}

	public TransactionTimestamp cleanCopy(Serializer journalSerializer) {
		return new TransactionTimestamp(_transactionCapsule.cleanCopy(), _systemVersion, _executionTime);
	}

}
