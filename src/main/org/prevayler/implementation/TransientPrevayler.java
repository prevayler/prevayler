package org.prevayler.implementation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.prevayler.Prevayler;
import org.prevayler.Transaction;


public class TransientPrevayler implements Prevayler {

    private final Object prevalentSystem;


    public TransientPrevayler(Object prevalentSystem) {
        this.prevalentSystem = prevalentSystem;
    }

	synchronized public void execute(Transaction transaction) {
		serializeInMemory(transaction).executeOn(prevalentSystem);
	}

	public Object prevalentSystem() {
        return prevalentSystem;
    }

	static private Transaction serializeInMemory(Transaction transaction) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			new ObjectOutputStream(buffer).writeObject(transaction);
			ByteArrayInputStream input = new ByteArrayInputStream(buffer.toByteArray()); 
			return (Transaction)(new ObjectInputStream(input).readObject());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected Exception. Serialization in memory should not fail.");
		}
	}

}
