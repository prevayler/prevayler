package org.prevayler.implementation.snapshot;

import org.prevayler.foundation.serialization.SerializationStrategy;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.foundation.serialization.Deserializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Collections;

public class GenericSnapshotManager extends AbstractSnapshotManager {

	private Map _strategies;
	private String _primaryStrategy;

	public GenericSnapshotManager(SerializationStrategy strategy, Object newPrevalentSystem, String snapshotDirectoryName)
			throws IOException, ClassNotFoundException {
		this(strategy, "snapshot", newPrevalentSystem, snapshotDirectoryName);
	}

	public GenericSnapshotManager(SerializationStrategy strategy, String suffix, Object newPrevalentSystem, String snapshotDirectoryName)
			throws IOException, ClassNotFoundException {
		this(Collections.singletonMap(suffix, strategy), suffix, newPrevalentSystem, snapshotDirectoryName);
	}

	// this is only here for NullSnapshotManager support
	GenericSnapshotManager(SerializationStrategy strategy, Object newPrevalentSystem) {
		_strategies = Collections.singletonMap("snapshot", strategy);
		_primaryStrategy = "snapshot";
		nullInit(newPrevalentSystem);
	}

	public GenericSnapshotManager(Map strategies, String primaryStrategy, Object newPrevalentSystem, String snapshotDirectoryName)
			throws IOException, ClassNotFoundException {
		_strategies = strategies;
		_primaryStrategy = primaryStrategy;
		init(newPrevalentSystem, snapshotDirectoryName);
	}

	protected String suffix() {
		return _primaryStrategy;
	}

	private SerializationStrategy primaryStrategy() {
		return (SerializationStrategy) _strategies.get(_primaryStrategy);
	}

	protected Object readSnapshot(File snapshotFile) throws ClassNotFoundException, IOException {
		String suffix = snapshotFile.getName().substring(snapshotFile.getName().indexOf('.') + 1);
		if (!_strategies.containsKey(suffix)) throw new IOException(snapshotFile.toString() + " cannot be read; only " + _strategies.keySet().toString() + " supported");

		SerializationStrategy strategy = (SerializationStrategy) _strategies.get(suffix);
        FileInputStream in = new FileInputStream(snapshotFile);
        try {
			Deserializer deserializer = strategy.createDeserializer(in);
			return deserializer.readObject();
        } finally { in.close(); }
	}

	public void writeSnapshot(Object prevalentSystem, OutputStream out) throws IOException {
		Serializer serializer = primaryStrategy().createSerializer(out);
		try {
			serializer.writeObject(prevalentSystem);
		} finally {
			serializer.flush();
		}
	}

	public Object readSnapshot(InputStream in) throws IOException, ClassNotFoundException {
		Deserializer deserializer = primaryStrategy().createDeserializer(in);
		return deserializer.readObject();
	}

}
