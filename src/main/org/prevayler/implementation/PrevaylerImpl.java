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


public class PrevaylerImpl implements Prevayler {

	private final Object _prevalentSystem;
	private long _systemVersion = 0;

	private final Clock _clock;

	private final SnapshotManager _snapshotManager;

	private final TransactionPublisher _publisher;
	private final TransactionSubscriber _subscriber = subscriber();
	private boolean _ignoreRuntimeExceptions;


	/** @param newPrevalentSystem The newly started, "empty" prevalent system that will be used as a starting point for every system startup, until the first snapshot is taken.
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
		_publisher.addSubscriber(_subscriber, _systemVersion + 1);
		_ignoreRuntimeExceptions = false;
	}


	public Clock clock() { return _clock; }


	public void execute(Transaction transaction) { _publisher.publish(transaction); }


	public Object execute(Query query) throws Exception {
		//TODO Guarantee that the clock will not advance during query execution. Logically, advancing the clock is the same as executing a "clock advance transaction". Any one who advances the clock, therefore, must synchronize on the _prevalentSystem too.
		synchronized (_prevalentSystem) {
			return query.query(_prevalentSystem, clock().time());
		}
	}


	public Object execute(TransactionWithQuery transactionWithQuery) throws Exception {
		TransactionWithQueryExecuter executer = new TransactionWithQueryExecuter(transactionWithQuery);
		execute(executer);
		return executer.result();
	}


	public Object prevalentSystem() { return _prevalentSystem; }


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
