package org.prevayler.implementation;

import org.prevayler.Transaction;
import org.prevayler.TransactionWithQuery;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class TransactionWithQueryCapsule implements Transaction {

    private static final Map _instancesById = new HashMap();
    private static int _nextId;
	private Integer _id;  //TODO Modify to final once Skaringa is removed.

	private TransactionWithQuery _contents;   //TODO Modify to final once Skaringa is removed.

	private transient Object _queryResult;
	private transient Exception _queryException;

	
	TransactionWithQueryCapsule() {}  //Necessary for Skaringa. //TODO Remove Skaringa support completely. Search all code for 'Skaringa' and remove parts that were included only to support Skaringa.
	
	TransactionWithQueryCapsule(TransactionWithQuery contents) {
		_id = new Integer(_nextId++);
	    _instancesById.put(_id, this);
		_contents = contents;
	}

    public void executeOn(Object prevalentSystem, Date executionTime) {
        TransactionWithQueryCapsule original = (TransactionWithQueryCapsule)_instancesById.get(_id); //This can be a deep copy executing.
		try {
			original._queryResult = _contents.executeAndQuery(prevalentSystem, executionTime);
		} catch (RuntimeException rx) {
			throw rx;   //This is necessary because of the rollback feature.
		} catch (Exception ex) {
		    original._queryException = ex;
		}
        
    }

	Object result() throws Exception {
		if (_queryException != null) throw _queryException;
		return _queryResult;
	}

}
