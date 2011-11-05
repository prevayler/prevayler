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
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.implementation.publishing.TransactionPublisher;
import org.prevayler.implementation.snapshot.GenericSnapshotManager;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class PrevaylerImpl<P extends Serializable> implements Prevayler<P>{

	private final PrevalentSystemGuard<P> _guard;
	private final Clock _clock;

	private final GenericSnapshotManager<P> _snapshotManager;

	private final TransactionPublisher _publisher;

	private final Serializer _journalSerializer;


	/** Creates a new Prevayler
	 * 
	 * @param snapshotManager The SnapshotManager that will be used for reading and writing snapshot files.
	 * @param transactionPublisher The TransactionPublisher that will be used for publishing transactions executed with this PrevaylerImpl.
	 * @param journalSerializer
	 */
	public PrevaylerImpl(GenericSnapshotManager<P> snapshotManager, TransactionPublisher transactionPublisher,
						 Serializer journalSerializer) throws IOException, ClassNotFoundException {
		_snapshotManager = snapshotManager;

		_guard = _snapshotManager.recoveredPrevalentSystem();

		_publisher = transactionPublisher;
		_clock = _publisher.clock();

		_guard.subscribeTo(_publisher);

		_journalSerializer = journalSerializer;
	}

	public P prevalentSystem() { return _guard.prevalentSystem(); }


	public Clock clock() { return _clock; }


	public void execute(Transaction<P> transaction) {
        publish(new TransactionCapsule<P>(transaction, _journalSerializer));    //TODO Optimizations: 1) The Censor can use the actual given transaction if it is Immutable instead of deserializing a new one from the byte array. 2) Make the baptism fail-fast feature optional (default is on). If it is off, the given transaction can be used instead of deserializing a new one from the byte array.
	}


	private void publish(Capsule capsule) {
		_publisher.publish(capsule);
	}


	public <R> R execute(Query<P,R> sensitiveQuery) throws Exception {
		return _guard.executeQuery(sensitiveQuery, clock());
	}


	public <R> R execute(TransactionWithQuery<P,R> transactionWithQuery) throws Exception {
		TransactionWithQueryCapsule<P,R> capsule = new TransactionWithQueryCapsule<P,R>(transactionWithQuery, _journalSerializer);
		publish(capsule);
		return capsule.result();
	}


	public <R> R execute(SureTransactionWithQuery<P,R> sureTransactionWithQuery) {
		try {
			return execute((TransactionWithQuery<P,R>)sureTransactionWithQuery);
		} catch (RuntimeException runtime) {
			throw runtime;
		} catch (Exception checked) {
			throw new RuntimeException("Unexpected Exception thrown.", checked);
		}
	}


	public File takeSnapshot() throws Exception {
		return _guard.takeSnapshot(_snapshotManager);
	}


	public void close() throws IOException { _publisher.close(); }

}
