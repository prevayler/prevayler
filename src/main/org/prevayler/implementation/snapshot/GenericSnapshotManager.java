package org.prevayler.implementation.snapshot;

import org.prevayler.foundation.serialization.SerializationStrategy;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.foundation.serialization.Deserializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class GenericSnapshotManager extends AbstractSnapshotManager {

	private SerializationStrategy _strategy;

	public GenericSnapshotManager(SerializationStrategy strategy, Object newPrevalentSystem, String snapshotDirectoryName)
			throws IOException, ClassNotFoundException {
		_strategy = strategy;
		init(newPrevalentSystem, snapshotDirectoryName);
	}

	// this is only here for NullSnapshotManager support
	GenericSnapshotManager(SerializationStrategy strategy, Object newPrevalentSystem) {
		_strategy = strategy;
		nullInit(newPrevalentSystem);
	}

	public void writeSnapshot(Object prevalentSystem, OutputStream out) throws IOException {
		Serializer serializer = _strategy.createSerializer(out);
		try {
			serializer.writeObject(prevalentSystem);
		} finally {
			serializer.flush();
		}
	}

	public Object readSnapshot(InputStream in) throws IOException, ClassNotFoundException {
		Deserializer deserializer = _strategy.createDeserializer(in);
		return deserializer.readObject();
	}

}
