// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001-2003 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import java.io.IOException;
import java.util.Date;

import org.prevayler.Clock;
import org.prevayler.Prevayler;
import org.prevayler.Query;
import org.prevayler.Transaction;
import org.prevayler.TransactionWithQuery;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.publishing.TransactionSubscriber;
import org.prevayler.implementation.snapshot.SnapshotManager;


public class PrevaylerImpl implements Prevayler {

	private final Object _prevalentSystem;
	private long _systemVersion = 0;

	private final Clock _clock;

	private final SnapshotManager _snapshotManager;

	private final TransactionPublisher _publisher;
	private boolean _ignoreRuntimeExceptions;


	/** Creates a new Prevayler
	 * @param snapshotManager The SnapshotManager that will be used for reading and writing snapshot files.
	 * @param transactionPublisher The TransactionPublisher that will be used for publishing transactions executed with this PrevaylerImpl.
	 */
	public PrevaylerImpl(SnapshotManager snapshotManager, TransactionPublisher transactionPublisher) throws IOException, ClassNotFoundException {
		_snapshotManager = snapshotManager;
		_prevalentSystem = _snapshotManager.recoveredPrevalentSystem();
		_systemVersion = _snapshotManager.recoveredVersion();

		_publisher = transactionPublisher;
		_clock = _publisher.clock();

		_ignoreRuntimeExceptions = true;     //During pending transaction recovery (rolling forward), RuntimeExceptions are ignored because they were already thrown and handled during the first transaction execution.
		_publisher.addSubscriber(subscriber(), _systemVersion + 1);
		_ignoreRuntimeExceptions = false;
	}


	public Object prevalentSystem() { return _prevalentSystem; }


	public Clock clock() { return _clock; }


	public void execute(Transaction transaction) { _publisher.publish(transaction); }


	public Object execute(Query query) throws Exception {
		synchronized (_prevalentSystem) {
			return query.query(_prevalentSystem, clock().time());
		}
	}


	public Object execute(TransactionWithQuery transactionWithQuery) throws Exception {
		TransactionWithQueryExecuter executer = new TransactionWithQueryExecuter(transactionWithQuery);
		execute(executer);
		return executer.result();
	}


	public void takeSnapshot() throws IOException {
	    synchronized (_prevalentSystem) {
	        _snapshotManager.writeSnapshot(_prevalentSystem, _systemVersion);
	    }
	}


	private TransactionSubscriber subscriber() {
		return new TransactionSubscriber() {
	
			public void receive(Transaction transaction, Date executionTime) {
				synchronized (_prevalentSystem) {
					_systemVersion++;
					try {
						transaction.executeOn(_prevalentSystem, executionTime);
					} catch (RuntimeException rx) {
						if (!_ignoreRuntimeExceptions) throw rx;
						rx.printStackTrace();
					}
				}
			}
	
		};
	}

}
