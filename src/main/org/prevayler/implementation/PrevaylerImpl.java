//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import org.prevayler.Clock;
import org.prevayler.Prevayler;
import org.prevayler.Query;
import org.prevayler.SureTransactionWithQuery;
import org.prevayler.Transaction;
import org.prevayler.TransactionWithQuery;
import org.prevayler.foundation.DeepCopier;
import org.prevayler.foundation.monitor.Monitor;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.publishing.TransactionSubscriber;
import org.prevayler.implementation.snapshot.GenericSnapshotManager;

import java.io.IOException;
import java.util.Date;

public class PrevaylerImpl implements Prevayler {

	private final Object _prevalentSystem;
	private long _systemVersion = 0;

	private final Clock _clock;

	private final GenericSnapshotManager _snapshotManager;

	private final TransactionPublisher _publisher;
	private boolean _ignoreRuntimeExceptions;
    private Monitor _monitor;

	private final Serializer _journalSerializer;


	/** Creates a new Prevayler
	 * 
	 * @param snapshotManager The SnapshotManager that will be used for reading and writing snapshot files.
	 * @param transactionPublisher The TransactionPublisher that will be used for publishing transactions executed with this PrevaylerImpl.
	 * @param prevaylerMonitor The Monitor that will be used to monitor interesting calls to this PrevaylerImpl.
	 * @param journalSerializer
	 */
	public PrevaylerImpl(GenericSnapshotManager snapshotManager, TransactionPublisher transactionPublisher,
						 Monitor prevaylerMonitor, Serializer journalSerializer) throws IOException, ClassNotFoundException {
	    _monitor = prevaylerMonitor;
		_snapshotManager = snapshotManager;

		_prevalentSystem = _snapshotManager.recoveredPrevalentSystem();
		
		_systemVersion = _snapshotManager.recoveredVersion();

		_publisher = transactionPublisher;
		_clock = _publisher.clock();

		_ignoreRuntimeExceptions = true;     //During pending transaction recovery (rolling forward), RuntimeExceptions are ignored because they were already thrown and handled during the first transaction execution.
		_publisher.addSubscriber(subscriber(), _systemVersion + 1);
		_ignoreRuntimeExceptions = false;

		_journalSerializer = journalSerializer;
	}

	public Object prevalentSystem() { return _prevalentSystem; }


	public Clock clock() { return _clock; }


	public void execute(Transaction transaction) {
        publish((Transaction)deepCopy(transaction));    //TODO Optimizations: 1) Publish the byte array of the serialized transaction (this will save the Censor and the Logger from having to serialize the transaction again). This is also a step towards transaction multiplexing (useful to avoid hickups due to very large transactions). The Censor can use the actual given transaction if it is Immutable instead of deserializing a new one from the byte array. 2) Make the baptism fail-fast feature optional (default is on). If it is off, the given transaction can be used instead of deserializing a new one from the byte array.
	}


	private void publish(Transaction transaction) {
		_publisher.publish(transaction);
	}


	public Object execute(Query sensitiveQuery) throws Exception {
		synchronized (_prevalentSystem) {
			return sensitiveQuery.query(_prevalentSystem, clock().time());
		}
	}


	public Object execute(TransactionWithQuery transactionWithQuery) throws Exception {
		TransactionWithQuery copy = (TransactionWithQuery)deepCopy(transactionWithQuery);
		TransactionWithQueryExecuter executer = new TransactionWithQueryExecuter(copy);
		publish(executer);
		return executer.result();
	}


	public Object execute(SureTransactionWithQuery sureTransactionWithQuery) {
		try {
			return execute((TransactionWithQuery)sureTransactionWithQuery);
		} catch (RuntimeException runtime) {
			throw runtime;
		} catch (Exception checked) {
			throw new RuntimeException("Unexpected Exception thrown.", checked);
		}
	}


	public void takeSnapshot() throws IOException {
	    synchronized (_prevalentSystem) {
	        _snapshotManager.writeSnapshot(_prevalentSystem, _systemVersion);
	    }
	}


	public void close() throws IOException { _publisher.close(); }

	private Object deepCopy(Object transaction) {
		try {
			return DeepCopier.deepCopy(transaction, _journalSerializer);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Unable to produce a deep copy of a transaction. Deep copies of transactions are executed instead of the transactions themselves so that the behaviour of the system during transaction execution is exactly the same as during transaction recovery from the journal.");
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
						if (!_ignoreRuntimeExceptions) throw rx;  //TODO Guarantee that transactions received from pending transaction recovery don't ever throw RuntimeExceptions. Maybe use a wrapper for that.
					}
				}
			}
	
		};
	}

}
